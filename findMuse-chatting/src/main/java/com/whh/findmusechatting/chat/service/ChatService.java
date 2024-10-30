package com.whh.findmusechatting.chat.service;

import com.whh.findmusechatting.chat.entity.*;
import com.whh.findmusechatting.chat.repository.ChatMessageRepository;
import com.whh.findmusechatting.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository messageRepository;
    private final KafkaTemplate<String, ChatMessage> messageKafkaTemplate;
    private final KafkaTemplate<String, ChatNotification> notificationKafkaTemplate;

    private final Map<String, Sinks.Many<ChatMessage>> messagesSinks;
    private final Map<String, Sinks.Many<ChatNotification>> notificationSinks;

    @Value("${spring.kafka.topic.message}")
    private String messageTopic;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    public Mono<ChatRoom> createChatRoom(ChatRoom chatRoom) {
        chatRoom.setCreatedAt(LocalDateTime.now());
        return chatRoomRepository.save(chatRoom)
                .doOnSuccess(saved -> {
                    messagesSinks.computeIfAbsent(saved.getId(),
                            id -> Sinks.many().multicast().onBackpressureBuffer());
                })
                .doOnError(throwable -> {
                    log.error("채팅방 생성 중 에러가 발생했습니다. : {}", throwable.getMessage());
                });
    }

    public Mono<ChatMessage> sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setMessageType(MessageType.CHAT);

        return Mono.fromSupplier(() -> {
            messageKafkaTemplate.send(messageTopic, message.getRoomId(), message);

//            Sinks.Many<ChatMessage> sink = messagesSinks.get(message.getRoomId());
//            if (sink != null) {
//                sink.tryEmitNext(message);
//            }
            return message;
        });
    }

    public Mono<Void> sendNotification(ChatMessage message) {
        return chatRoomRepository.findById(message.getSenderId())
                .flatMap(room -> {
                    List<Mono<Void>> notifications = room.getParticipants().stream()
                            .filter(participantId -> !participantId.equals(message.getSenderId()))
                            .map(receiverId -> createAndSendNotification(message, receiverId))
                            .collect(Collectors.toList());

                    return Mono.when(notifications);
                });
    }

    private Mono<Void> createAndSendNotification(ChatMessage message, String receiverId) {
        return Mono.fromRunnable(() -> {
            ChatNotification notification = ChatNotification.builder()
                    .senderId(message.getSenderId())
                    .senderName(message.getSenderName())
                    .receiverId(receiverId)
                    .content(message.getContent())
                    .roomId(message.getRoomId())
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationKafkaTemplate.send(notificationTopic,
                    notification.getReceiverId(), notification);
        });
    }

    // 채팅방의 메시지 스트림 구독
    public Flux<ChatMessage> getChatMessages(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampDesc(roomId)
                .mergeWith(messagesSinks.computeIfAbsent(roomId,
                        id -> Sinks.many().multicast().onBackpressureBuffer()).asFlux());
    }

    // 사용자의 알림 스트림 구독
    public Flux<ChatNotification> getUserNotifications(String userId) {
        return notificationSinks.computeIfAbsent(userId,
                id -> Sinks.many().multicast().onBackpressureBuffer()).asFlux();
    }

    // 채팅방 참여
    public Mono<ChatRoom> joinChatRoom(String roomId, String userId) {
        return chatRoomRepository.findById(roomId)
                .flatMap(chatRoom -> {
                    // 이미 참여한 사용자인지 확인
                    if (chatRoom.getParticipants().contains(userId)) {
                        return Mono.error(new IllegalStateException("이미 참여한 채팅방입니다."));
                    }

                    // 참여자 목록에 추가
                    chatRoom.getParticipants().add(userId);

                    // 시스템 메시지 생성
                    ChatMessage systemMessage = ChatMessage.builder()
                            .roomId(roomId)
                            .senderId("SYSTEM")
                            .senderName("SYSTEM")
                            .content(userId + "님이 입장하셨습니다.")
                            .timestamp(LocalDateTime.now())
                            .messageType(MessageType.SYSTEM)
                            .build();

                    return chatRoomRepository.save(chatRoom)
                            .flatMap(savedRoom ->
                                    sendMessage(systemMessage)
                                            .thenReturn(savedRoom));
                });
    }

    // 채팅방 나가기
    public Mono<ChatRoom> leaveChatRoom(String roomId, String userId) {
        return chatRoomRepository.findById(roomId)
                .flatMap(chatRoom -> {
                    // 참여자가 아닌 경우
                    if (!chatRoom.getParticipants().contains(userId)) {
                        return Mono.error(new IllegalStateException("참여하지 않은 채팅방입니다."));
                    }

                    // 참여자 목록에서 제거
                    chatRoom.getParticipants().remove(userId);

                    // 시스템 메시지 생성
                    ChatMessage systemMessage = ChatMessage.builder()
                            .roomId(roomId)
                            .senderId("SYSTEM")
                            .senderName("SYSTEM")
                            .content(userId + "님이 퇴장하셨습니다.")
                            .timestamp(LocalDateTime.now())
                            .messageType(MessageType.SYSTEM)
                            .build();

                    return chatRoomRepository.save(chatRoom)
                            .flatMap(savedRoom ->
                                    sendMessage(systemMessage)
                                            .thenReturn(savedRoom));
                });
    }

    // 채팅방 삭제
    public Mono<Void> deleteChatRoom(String roomId, String userId) {
        return chatRoomRepository.findById(roomId)
                .flatMap(chatRoom -> {
                    // 방장이 아닌 경우
                    if (!chatRoom.getOwner().equals(userId)) {
                        return Mono.error(new IllegalStateException("방장만 채팅방을 삭제할 수 있습니다."));
                    }

                    // 채팅방 관련 모든 메시지 삭제
                    return messageRepository.deleteByRoomId(roomId)
                            .then(chatRoomRepository.delete(chatRoom))
                            .then(Mono.fromRunnable(() -> {
                                // Sink 제거
                                messagesSinks.remove(roomId);

                                // 참여자들의 알림 발송
                                ChatNotification deleteNotification = ChatNotification.builder()
                                        .type(NotificationType.ROOM_DELETED)
                                        .roomId(roomId)
                                        .content("채팅방이 삭제되었습니다.")
                                        .timestamp(LocalDateTime.now())
                                        .build();

                                chatRoom.getParticipants().forEach(participantId ->
                                        notificationKafkaTemplate.send(notificationTopic,
                                                participantId, deleteNotification));
                            }));
                });
    }
}