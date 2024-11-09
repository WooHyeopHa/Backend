package com.whh.findmusechatting.kafka.consumer;

import com.whh.findmusechatting.chat.dto.response.ChatMessageResponse;
import com.whh.findmusechatting.chat.entity.ChatNotification;
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
    private final KafkaReceiver<String, ChatMessageResponse> messageKafkaReceiver;
    private final KafkaReceiver<String, ChatNotification> notificationKafkaReceiver;
    private final SinkConfiguration sinkConfiguration;
    private final Map<String, Sinks.Many<ChatMessageResponse>> messagesSinks;
    private final Map<String, Sinks.Many<ChatNotification>> notificationSinks;

    @PostConstruct
    public void init() {
        consumeMessages();
        consumeNotifications();
    }

    private void consumeMessages() {
        messageKafkaReceiver.receive()
                .doOnNext(record -> log.info("Received message: {}", record.value()))
                .flatMap(record -> {
                    ChatMessageResponse response = record.value();

                    Sinks.Many<ChatMessageResponse> sink = sinkConfiguration
                            .getOrCreateMessageSink(messagesSinks, response.roomId());
                    sink.tryEmitNext(response);

                    return Mono.fromRunnable(() -> record.receiverOffset().acknowledge());
                })
                .doOnError(error -> log.error("Error processing message: ", error))
                .retry()
                .subscribe();
    }

    private void consumeNotifications() {
        notificationKafkaReceiver.receive()
                .doOnNext(record -> log.info("Received notification: {}", record.value()))
                .flatMap(record -> Mono.fromRunnable(() -> {
                    ChatNotification notification = record.value();

                    Sinks.Many<ChatNotification> sink = sinkConfiguration
                            .getOrCreateNotificationSink(notificationSinks, notification.getReceiverId());

                    sink.tryEmitNext(notification);
                    record.receiverOffset().acknowledge();
                }))
                .doOnError(error -> log.error("Error processing notification: ", error))
                .retry()
                .subscribe();
    }
}