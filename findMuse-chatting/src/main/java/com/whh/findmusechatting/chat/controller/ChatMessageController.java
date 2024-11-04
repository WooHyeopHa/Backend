package com.whh.findmusechatting.chat.controller;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import com.whh.findmusechatting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatService chatService;

    @MessageMapping("chat.messages.{roomId}")
    public Mono<Void> sendMessage(@DestinationVariable String roomId, ChatMessage message) {
        message.setRoomId(roomId);
        log.info(message.toString());
        return chatService.sendMessage(message)
                .doOnSuccess(msg -> log.info("전송된 메시지 : {}", msg))
                .doOnError(error -> log.error("메시지 전송에 실패했습니다 : {}", error.getMessage()))
                .then(chatService.sendNotification(message));
    }

    @MessageMapping("chat.subscribe.{roomId}")
    public Flux<ChatMessage> subscribeToChatRoom(@DestinationVariable String roomId) {
        return chatService.getChatMessages(roomId)
                .doOnNext(message -> log.info("스트리밍 메시지 : {}", message))
                .doOnError(error -> log.error("메시지 구독에 실패했습니다 : {}", error.getMessage()));
    }

    @MessageMapping("notifications.subscribe.{userId}")
    public Flux<ChatNotification> subscribeToNotifications(@DestinationVariable String userId) {
        return chatService.getUserNotifications(userId)
                .doOnNext(notification -> log.info("스트리밍 알림: {}", notification))
                .doOnError(error -> log.error("알림 구독에 실패했습니다 : {}", error.getMessage()));
    }
}
