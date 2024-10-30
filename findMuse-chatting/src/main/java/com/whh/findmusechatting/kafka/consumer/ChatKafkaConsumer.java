package com.whh.findmusechatting.kafka.consumer;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import com.whh.findmusechatting.chat.repository.ChatMessageRepository;
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

    private final ChatMessageRepository messageRepository;
    private final Map<String, Sinks.Many<ChatMessage>> messagesSinks;
    private final Map<String, Sinks.Many<ChatNotification>> notificationSinks;

    private final KafkaReceiver<String, ChatMessage> messageKafkaReceiver;
    private final KafkaReceiver<String, ChatNotification> notificationKafkaReceiver;

    @PostConstruct
    public void consume() {
        // 메시지 컨슈머
        messageKafkaReceiver.receive()
                .doOnNext(record -> log.info("Received message: {}", record.value()))
                .flatMap(record -> {
                    ChatMessage chatMessage = record.value();
                    return messageRepository.save(chatMessage)
                            .doOnSuccess(savedMessage -> {
                                Sinks.Many<ChatMessage> sink = messagesSinks.computeIfAbsent(savedMessage.getRoomId(),
                                        id -> Sinks.many().multicast().onBackpressureBuffer());
                                sink.tryEmitNext(chatMessage);
                            })
                            .then(Mono.fromRunnable(() -> record.receiverOffset().acknowledge()));
                })
                .doOnError(error -> {
                    log.error("Error processing message: ", error);
                })
                .retry()
                .subscribe();

        // 알림 컨슈머
        notificationKafkaReceiver.receive()
                .doOnNext(record -> log.info("Received notification: {}", record.value()))
                .doOnNext(record -> {
                    ChatNotification notification = record.value();
                    Sinks.Many<ChatNotification> sink = notificationSinks.get(notification.getReceiverId());
                    if (sink != null) {
                        sink.tryEmitNext(notification);
                    }
                    record.receiverOffset().acknowledge();
                })
                .doOnError(error -> {
                    log.error("Error processing notification: ", error);
                })
                .retry() // 에러 발생 시 재시도
                .subscribe();
    }
}