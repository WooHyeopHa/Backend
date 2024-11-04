package com.whh.findmusechatting.chat.service;

import com.whh.findmusechatting.chat.entity.*;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import com.whh.findmusechatting.chat.repository.ChatMessageRepository;
import com.whh.findmusechatting.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final Map<String, Sinks.Many<ChatMessage>> messagesSinks;
    private final Map<String, Sinks.Many<ChatNotification>> notificationSinks;

    @Value("${spring.kafka.topic.message}")
    private String messageTopic;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    @Description("메시지 보내기")
    public Mono<ChatMessage> sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setMessageType(MessageType.CHAT);

        return Mono.fromSupplier(() -> {
            messageKafkaTemplate.send(messageTopic, message.getRoomId(), message);
            return message;
        });
    }

    @Description("채팅방 참여자에게 메시지 알림 보내기")
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

    @Description("Kafka에 알린 전송")
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

    @Description("채팅방 메시지 스트림 구독")
    public Flux<ChatMessage> getChatMessages(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId)
                .mergeWith(messagesSinks.computeIfAbsent(roomId,
                        id -> Sinks.many().multicast().onBackpressureBuffer()).asFlux());
    }

    @Description("page에 해당하는 채팅방 메시지 목록 가져오기")
    public Flux<ChatMessage> getPaginatedChatMessages(String roomId, int page, int size) {
        Query query = new Query(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.ASC, "timestamp"))
                .with(PageRequest.of(page, size));

        return reactiveMongoTemplate.find(query, ChatMessage.class);
    }

    @Description("채팅방 조회")
    public Flux<ChatMessage> getChatMessagesWithStreaming(String roomId, int page, int size) {
        Flux<ChatMessage> paginatedMessages = getPaginatedChatMessages(roomId, page, size);

        // paginatedMessages의 마지막 타임스탬프 얻기
        Mono<LocalDateTime> lastTimestamp = paginatedMessages
                .last()
                .map(ChatMessage::getTimestamp)
                .defaultIfEmpty(LocalDateTime.MIN);

        // lastTimestamp 이후의 새로운 메시지만 구독
        Flux<ChatMessage> newMessages = lastTimestamp.flatMapMany(timestamp ->
                messagesSinks.computeIfAbsent(roomId, id -> Sinks.many().multicast().onBackpressureBuffer())
                        .asFlux()
                        .filter(message -> message.getTimestamp().isAfter(timestamp))
        );

        return paginatedMessages.concatWith(newMessages);
    }

    @Description("유저 채팅방 알림 등록")
    public Flux<ChatNotification> getUserNotifications(String userId) {
        return notificationSinks.computeIfAbsent(userId,
                id -> Sinks.many().multicast().onBackpressureBuffer()).asFlux();
    }
}