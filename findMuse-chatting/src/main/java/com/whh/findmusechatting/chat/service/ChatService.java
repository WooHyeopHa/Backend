package com.whh.findmusechatting.chat.service;

import com.whh.findmusechatting.chat.dto.request.CreateChatMessageRequest;
import com.whh.findmusechatting.chat.dto.response.ChatMessageResponse;
import com.whh.findmusechatting.chat.entity.*;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import com.whh.findmusechatting.chat.repository.ChatMessageRepository;
import com.whh.findmusechatting.chat.repository.ChatRoomRepository;
import com.whh.findmusechatting.common.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.codec.multipart.FilePart;
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
    private final KafkaTemplate<String, ChatMessageResponse> messageKafkaTemplate;
    private final KafkaTemplate<String, ChatNotification> notificationKafkaTemplate;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final MysqlUserService mysqlUserService;
    private final S3Util s3Util;

    private final Map<String, Sinks.Many<ChatMessage>> messagesSinks;
    private final Map<String, Sinks.Many<ChatNotification>> notificationSinks;

    @Value("${spring.kafka.topic.message}")
    private String messageTopic;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    @Description("메시지 전송")
    public Mono<ChatMessage> sendMessage(CreateChatMessageRequest message) {
        Mono<ChatMessage> chatMessage = messageRepository.save(ChatMessage.of(message));
        Mono<ChatMessageResponse.UserInfo> userInfo = mysqlUserService.findUserInfoById(message.senderId());

        return Mono.zip(chatMessage, userInfo)
                .flatMap(tuple -> {
                    ChatMessage savedMessage = tuple.getT1();
                    ChatMessageResponse.UserInfo senderInfo = tuple.getT2();

                    // ChatMessageResponse 생성
                    ChatMessageResponse response = ChatMessageResponse.from(savedMessage, senderInfo);

                    // Kafka에 메시지 전송
                    return Mono.fromSupplier(() -> {
                        messageKafkaTemplate.send(messageTopic, response.roomId(), response);
                        return savedMessage;
                    });
                });
    }

    public Mono<ChatMessage> sendImageMessage(FilePart filePart, String roomId, String senderId) {
        return s3Util.uploadFile(filePart, "image")
                .flatMap(file -> {
                    CreateChatMessageRequest imageMessage = CreateChatMessageRequest.builder()
                            .roomId(roomId)
                            .senderId(senderId)
                            .content(file.getFileDetail().getUrl())
                            .messageType(MessageType.IMAGE)
                            .imageDetails(ChatMessage.ImageDetails.builder()
                                    .originalFileName(file.getName())
                                    .contentType(file.getContentType())
                                    .fileSize(filePart.headers().getContentLength())
                                    .thumbnailUrl(file.getFileDetail().getUrl())
                                    .build())
                            .build();

                    return sendMessage(imageMessage);
                });
    }

    @Description("시스템 메시지 전송")
    public Mono<ChatMessage> sendSystemMessage(ChatMessage message) {
        Mono<ChatMessage> chatMessage = messageRepository.save(message);

        return chatMessage.flatMap(savedMessage -> {
            ChatMessageResponse response = ChatMessageResponse.from(savedMessage, ChatMessageResponse.UserInfo.getSystemInfo());

            return Mono.fromSupplier(() -> {
                messageKafkaTemplate.send(messageTopic, response.roomId(), response);
                return savedMessage;
            });
        });
    }


    @Description("채팅방 참여자에게 메시지 알림 보내기")
    public Mono<Void> sendNotification(CreateChatMessageRequest message) {
        return chatRoomRepository.findById(message.senderId())
                .flatMap(room -> {
                    List<Mono<Void>> notifications = room.getParticipants().stream()
                            .filter(participant -> !participant.getId().equals(message.senderId()))
                            .map(participant -> createAndSendNotification(message, participant.getId()))
                            .collect(Collectors.toList());

                    return Mono.when(notifications);
                });
    }

    @Description("Kafka에 알린 전송")
    private Mono<Void> createAndSendNotification(CreateChatMessageRequest message, String receiverId) {
        return Mono.fromRunnable(() -> {
            ChatNotification notification = ChatNotification.builder()
                    .senderId(message.senderId())
                    .receiverId(receiverId)
                    .content(message.content())
                    .roomId(message.roomId())
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
    public Flux<ChatMessageResponse> getPaginatedChatMessages(String roomId, int page, int size) {
        Query query = new Query(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.ASC, "timestamp"))
                .with(PageRequest.of(page, size));

        return reactiveMongoTemplate.find(query, ChatMessage.class)
                .flatMap(message -> mysqlUserService.findUserInfoById(message.getSenderId())
                        .map(userInfo -> ChatMessageResponse.from(message, userInfo)));
    }

    @Description("채팅 조회")
    public Flux<ChatMessageResponse> getChatMessagesWithStreaming(String roomId, int page, int size) {
        Flux<ChatMessageResponse> paginatedMessages = getPaginatedChatMessages(roomId, page, size);

        // paginatedMessages의 마지막 타임스탬프 얻기
        Mono<LocalDateTime> lastTimestamp = paginatedMessages
                .last()
                .map(ChatMessageResponse::timestamp)
                .defaultIfEmpty(LocalDateTime.MIN);

        // lastTimestamp 이후의 새로운 메시지만 구독
        Flux<ChatMessageResponse> newMessages = lastTimestamp.flatMapMany(timestamp ->
                messagesSinks.computeIfAbsent(roomId, id -> Sinks.many().multicast().onBackpressureBuffer())
                        .asFlux()
                        .filter(message -> message.getTimestamp().isAfter(timestamp))
                        .flatMap(message -> mysqlUserService.findUserInfoById(message.getSenderId())
                                .map(userInfo -> ChatMessageResponse.from(message, userInfo)))
        );

        return paginatedMessages.concatWith(newMessages);
    }

    @Description("유저 채팅방 알림 등록")
    public Flux<ChatNotification> getUserNotifications(String userId) {
        return notificationSinks.computeIfAbsent(userId,
                id -> Sinks.many().multicast().onBackpressureBuffer()).asFlux();
    }
}