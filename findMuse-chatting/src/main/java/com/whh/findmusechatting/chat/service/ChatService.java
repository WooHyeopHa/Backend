package com.whh.findmusechatting.chat.service;

import com.whh.findmusechatting.appointment.Appointment;
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
    private final KafkaTemplate<String, CreateChatMessageRequest> messageKafkaTemplate;
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
    public Mono<Void> sendMessage(CreateChatMessageRequest message) {
        return Mono.fromRunnable(() -> {
            messageKafkaTemplate.send(messageTopic, message.roomId(), message);
        });
    }
    
    @Description("이미지 메시지 전송")
    public Mono<Void> sendImageMessage(FilePart filePart, String roomId, String senderId) {
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
    
    @Description("약속 메시지 전송")
    public Mono<Void> sendAppointmentMessage(Appointment appointment) {
        CreateChatMessageRequest appointmentMessage = CreateChatMessageRequest.builder()
            .roomId(appointment.getRoomId())
            .messageType(MessageType.APPOINTMENT)
            .appointmentDetails(ChatMessage.AppointmentDetails.from(appointment))
            .build();
        
        return sendMessage(appointmentMessage);
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
    
    @Description("page에 해당하는 채팅방 메시지 목록 가져오기")
    public Flux<ChatMessageResponse> getPaginatedChatMessages(String roomId, int page, int size) {
        Query query = new Query(Criteria.where("roomId").is(roomId))
            .with(Sort.by(Sort.Direction.ASC, "timestamp"))
            .with(PageRequest.of(page, size));
        
        return reactiveMongoTemplate.find(query, ChatMessage.class)
            .flatMap(message -> mysqlUserService.findUserInfoById(message.getSenderId(), message.getMessageType())
                .map(userInfo -> ChatMessageResponse.from(message, userInfo)));
    }
    
    @Description("채팅 조회 with 스트리밍")
    public Flux<ChatMessageResponse> getChatMessagesWithStreaming(String roomId, int page, int size) {
        // 1. 페이지네이션된 기존 메시지 조회
        Flux<ChatMessageResponse> paginatedMessages = getPaginatedChatMessages(roomId, page, size);
        
        // 2. 마지막 메시지의 타임스탬프 추출 (없으면 최소 시간)
        Mono<LocalDateTime> lastTimestamp = paginatedMessages
            .last()
            .map(ChatMessageResponse::timestamp)
            .defaultIfEmpty(LocalDateTime.MIN);
        
        // 3. 실시간 메시지 스트림 생성
        Flux<ChatMessageResponse> liveMessages = lastTimestamp
            .flatMapMany(timestamp ->
                messagesSinks.computeIfAbsent(roomId, k -> Sinks.many().multicast().onBackpressureBuffer())
                    .asFlux()
                    .filter(message -> message.getTimestamp().isAfter(timestamp))
                    .flatMap(message ->
                        mysqlUserService.findUserInfoById(message.getSenderId(), message.getMessageType())
                            .map(userInfo -> ChatMessageResponse.from(message, userInfo))
                    )
            );
        
        // 4. 기존 메시지와 실시간 메시지 결합
        return Flux.concat(paginatedMessages, liveMessages);
    }

    @Description("유저 채팅방 알림 등록")
    public Flux<ChatNotification> getUserNotifications(String userId) {
        return notificationSinks.computeIfAbsent(userId,
                id -> Sinks.many().multicast().onBackpressureBuffer()).asFlux();
    }
}