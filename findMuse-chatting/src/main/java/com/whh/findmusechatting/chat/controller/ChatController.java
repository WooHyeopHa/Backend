package com.whh.findmusechatting.chat.controller;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import com.whh.findmusechatting.chat.entity.ChatRoom;
import com.whh.findmusechatting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("chat.createRoom")
    public Mono<ChatRoom> createRoom(ChatRoom chatRoom) {
        return chatService.createChatRoom(chatRoom)
                .doOnSuccess(room -> log.info("채팅방이 생성되었습니다 : {}", room))
                .doOnError(error -> log.error("채팅방 생성에 실패했습니다 : {}", error.getMessage()));
    }

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

    @MessageMapping("chat.join.{roomId}")
    public Mono<ChatRoom> joinRoom(@DestinationVariable String roomId, @Payload String userId) {
        return chatService.joinChatRoom(roomId, userId)
                .doOnSuccess(room -> log.info("사용자 {} 가 채팅방 {} 에 참여했습니다", userId, roomId))
                .doOnError(error -> log.error("채팅방 참여 실패: {}", error.getMessage()));
    }

    @MessageMapping("chat.leave.{roomId}")
    public Mono<ChatRoom> leaveRoom(@DestinationVariable String roomId, @Payload String userId) {
        return chatService.leaveChatRoom(roomId, userId)
                .doOnSuccess(room -> log.info("사용자 {} 가 채팅방 {} 를 나갔습니다", userId, roomId))
                .doOnError(error -> log.error("채팅방 나가기 실패: {}", error.getMessage()));
    }

    @MessageMapping("chat.delete.{roomId}")
    public Mono<Void> deleteRoom(@DestinationVariable String roomId, @Payload String userId) {
        return chatService.deleteChatRoom(roomId, userId)
                .doOnSuccess(unused -> log.info("채팅방 {} 이 삭제되었습니다", roomId))
                .doOnError(error -> log.error("채팅방 삭제 실패: {}", error.getMessage()));
    }
}
