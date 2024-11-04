package com.whh.findmusechatting.chat.controller;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import com.whh.findmusechatting.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatService chatService;

    @Operation(summary = "채팅방에 채팅 보내기")
    @MessageMapping("chat.messages.{roomId}")
    public Mono<Void> sendMessage(@DestinationVariable String roomId, ChatMessage message) {
        message.setRoomId(roomId);
        log.info(message.toString());
        return chatService.sendMessage(message)
                .doOnSuccess(msg -> log.info("전송된 메시지 : {}", msg))
                .doOnError(error -> log.error("메시지 전송에 실패했습니다 : {}", error.getMessage()))
                .then(chatService.sendNotification(message));
    }

    @Operation(summary = "채팅방에 클릭해서 메시지 내용 조회하고, 채팅방 구독하기")
    @MessageMapping("chat.subscribe.{roomId}")
    public Flux<ChatMessage> subscribeToChatRoom(@DestinationVariable String roomId,
                                                 @RequestParam int page,
                                                 @RequestParam int size) {
        return chatService.getChatMessagesWithStreaming(roomId, page, size)
                .doOnNext(message -> log.info("스트리밍 메시지 : {}", message))
                .doOnError(error -> log.error("메시지 구독에 실패했습니다 : {}", error.getMessage()));
    }

    @Operation(summary = "채팅방 알람 등록하기")
    @MessageMapping("notifications.subscribe.{userId}")
    public Flux<ChatNotification> subscribeToNotifications(@DestinationVariable String userId) {
        return chatService.getUserNotifications(userId)
                .doOnNext(notification -> log.info("스트리밍 알림: {}", notification))
                .doOnError(error -> log.error("알림 구독에 실패했습니다 : {}", error.getMessage()));
    }
}
