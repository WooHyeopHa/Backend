package com.whh.findmusechatting.chat.controller;

import com.whh.findmusechatting.chat.entity.ChatRoom;
import com.whh.findmusechatting.chat.service.ChatRoomService;
import com.whh.findmusechatting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public Mono<ChatRoom> createRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createChatRoom(chatRoom)
                .doOnSuccess(room -> log.info("채팅방이 생성되었습니다 : {}", room))
                .doOnError(error -> log.error("채팅방 생성에 실패했습니다 : {}", error.getMessage()));
    }

    @PostMapping("/{roomId}/join")
    public Mono<ChatRoom> joinRoom(@PathVariable String roomId, @RequestBody String userId) {
        return chatRoomService.joinChatRoom(roomId, userId)
                .doOnSuccess(room -> log.info("사용자 {} 가 채팅방 {} 에 참여했습니다", userId, roomId))
                .doOnError(error -> log.error("채팅방 참여 실패: {}", error.getMessage()));
    }

    @PostMapping("/{roomId}/leave")
    public Mono<ChatRoom> leaveRoom(@PathVariable String roomId, @RequestBody String userId) {
        return chatRoomService.leaveChatRoom(roomId, userId)
                .doOnSuccess(room -> log.info("사용자 {} 가 채팅방 {} 를 나갔습니다", userId, roomId))
                .doOnError(error -> log.error("채팅방 나가기 실패: {}", error.getMessage()));
    }

    @DeleteMapping("/{roomId}/delete")
    public Mono<Void> deleteRoom(@PathVariable String roomId, @RequestBody String userId) {
        return chatRoomService.deleteChatRoom(roomId, userId)
                .doOnSuccess(unused -> log.info("채팅방 {} 이 삭제되었습니다", roomId))
                .doOnError(error -> log.error("채팅방 삭제 실패: {}", error.getMessage()));
    }
}
