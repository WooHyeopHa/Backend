package com.whh.findmusechatting.chat.controller;

import com.whh.findmusechatting.chat.dto.request.CreateChatMessageRequest;
import com.whh.findmusechatting.chat.dto.response.ChatMessageResponse;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import com.whh.findmusechatting.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatService chatService;

    @Operation(summary = "채팅방에 채팅 보내기")
    @MessageMapping("chat.messages")
    public Mono<Void> sendMessage(CreateChatMessageRequest message) {
        return chatService.sendMessage(message)
                .doOnError(error -> log.error("메시지 전송에 실패했습니다 : {}", error.getMessage()))
                .then(chatService.sendNotification(message));
    }

    @Operation(summary = "채팅방에 이미지 업로드")
    @MessageMapping("chat.images")
    public Mono<Void> uploadImage(@RequestPart("file") FilePart filePart,
                                  @RequestParam("roomId") String roomId,
                                  @RequestParam("senderId") String senderId
    ) {
        return chatService.sendImageMessage(filePart, roomId, senderId)
                .doOnSuccess(msg -> log.info("이미지 업로드 완료 : {}", msg))
                .doOnError(error -> log.error("이미지 업로드 실패 : {}", error.getMessage()))
                .then(chatService.sendNotification(CreateChatMessageRequest.builder()
                        .roomId(roomId)
                        .senderId(senderId)
                        .messageType(MessageType.IMAGE)
                        .build()));
    }

    @Operation(summary = "채팅방에 클릭해서 메시지 내용 조회하고, 채팅방 구독하기")
    @MessageMapping("chat.subscribe.{roomId}")
    public Flux<ChatMessageResponse> subscribeToChatRoom(@DestinationVariable String roomId,
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
