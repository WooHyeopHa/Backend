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

    public Mono<ChatMessage> sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setMessageType(MessageType.CHAT);

        return Mono.fromSupplier(() -> {
            messageKafkaTemplate.send(messageTopic, message.getRoomId(), message);
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
}