package com.whh.findmusechatting.kafka.consumer;

import com.whh.findmusechatting.chat.dto.request.CreateChatMessageRequest;
import com.whh.findmusechatting.chat.dto.response.ChatMessageResponse;
import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import com.whh.findmusechatting.chat.repository.ChatMessageRepository;
import com.whh.findmusechatting.chat.service.MysqlUserService;
import com.whh.findmusechatting.common.config.SinkConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.KafkaReceiver;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatKafkaConsumer {
    private final KafkaReceiver<String, CreateChatMessageRequest> messageKafkaReceiver;
    private final KafkaReceiver<String, ChatNotification> notificationKafkaReceiver;
    private final SinkConfiguration sinkConfiguration;
    private final Map<String, Sinks.Many<ChatMessage>> messagesSinks;
    private final Map<String, Sinks.Many<ChatNotification>> notificationSinks;
    
    private final MysqlUserService mysqlUserService;
    private final ChatMessageRepository messageRepository;
    @PostConstruct
    public void init() {
        consumeMessages();
        consumeNotifications();
    }
    
    private void consumeMessages() {
        messageKafkaReceiver.receive()
            .doOnNext(record -> log.info("Kafka ChatMessage Consume : {}", record.value()))
            .flatMap(record -> {
                CreateChatMessageRequest messageRequest = record.value();
                
                return mysqlUserService.findUserInfoById(messageRequest.senderId(), messageRequest.messageType())
                    .flatMap(userInfo -> {
                        
                        log.info("User Info : " + userInfo.toString());
                        ChatMessage chatMessage = ChatMessage.of(messageRequest);
                        return messageRepository.save(chatMessage)
                            .flatMap(savedMessage -> {
                                ChatMessageResponse response = ChatMessageResponse.from(savedMessage, userInfo);
                                
                                Sinks.Many<ChatMessage> sink = sinkConfiguration
                                    .getOrCreateMessageSink(messagesSinks, response.roomId());
                                sink.tryEmitNext(savedMessage);
                                
                                return Mono.fromRunnable(() -> record.receiverOffset().acknowledge());
                            });
                    });
            })
            .doOnError(error -> log.error("채팅 메시지 처리 중 에러 발생 : ", error))
            .retry()
            .subscribe();
    }

    private void consumeNotifications() {
        notificationKafkaReceiver.receive()
                .doOnNext(record -> log.info("Kafka Notification Consume: {}", record.value()))
                .flatMap(record -> Mono.fromRunnable(() -> {
                    ChatNotification notification = record.value();

                    Sinks.Many<ChatNotification> sink = sinkConfiguration
                            .getOrCreateNotificationSink(notificationSinks, notification.getReceiverId());

                    sink.tryEmitNext(notification);
                    record.receiverOffset().acknowledge();
                }))
                .doOnError(error -> log.error("알림 처리 중 에러 발생: ", error))
                .retry()
                .subscribe();
    }
}