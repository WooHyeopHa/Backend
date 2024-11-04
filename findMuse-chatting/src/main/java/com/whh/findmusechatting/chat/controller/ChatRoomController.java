package com.whh.findmusechatting.chat.controller;

import com.whh.findmusechatting.chat.dto.response.ChatRoomResponse;
import com.whh.findmusechatting.chat.dto.request.ChatRoomUpdateRequest;
import com.whh.findmusechatting.chat.entity.ChatRoom;
import com.whh.findmusechatting.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 목록 조회하기")
    @GetMapping("/{userId}/list")
    public Flux<ChatRoomResponse> getUserChatRooms(@PathVariable String userId) {
        return chatRoomService.getUserChatRooms(userId);
    }

    @Operation(summary = "채팅방 생성하기")
    @PostMapping
    public Mono<ChatRoom> createRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createChatRoom(chatRoom)
                .doOnSuccess(room -> log.info("채팅방이 생성되었습니다 : {}", room))
                .doOnError(error -> log.error("채팅방 생성에 실패했습니다 : {}", error.getMessage()));
    }

    @Operation(summary = "채팅방 참여하기")
    @PostMapping("/{roomId}/join")
    public Mono<ChatRoom> joinRoom(@PathVariable String roomId, @RequestBody String userId) {
        return chatRoomService.joinChatRoom(roomId, userId)
                .doOnSuccess(room -> log.info("사용자 {} 가 채팅방 {} 에 참여했습니다", userId, roomId))
                .doOnError(error -> log.error("채팅방 참여 실패: {}", error.getMessage()));
    }

    @Operation(summary = "채팅방 수정하기")
    @PatchMapping("/{roomId}/update")
    public Mono<ChatRoom> updateRoom(@PathVariable String roomId,
                                     @RequestBody ChatRoomUpdateRequest request) {
        return chatRoomService.updateChatRoom(roomId, request)
                .doOnSuccess(room -> log.info("채팅방이 업데이트 되었습니다. : {}", room))
                .doOnError(error -> log.error("채팅방 업데이트에 실패했습니다 : {}", error.getMessage()));
    }

    @Operation(summary = "채팅방 나가기")
    @PostMapping("/{roomId}/leave")
    public Mono<ChatRoom> leaveRoom(@PathVariable String roomId, @RequestBody String userId) {
        return chatRoomService.leaveChatRoom(roomId, userId)
                .doOnSuccess(room -> log.info("사용자 {} 가 채팅방 {} 를 나갔습니다", userId, roomId))
                .doOnError(error -> log.error("채팅방 나가기 실패: {}", error.getMessage()));
    }

    @Operation(summary = "채팅방 삭제하기")
    @DeleteMapping("/{roomId}/delete")
    public Mono<Void> deleteRoom(@PathVariable String roomId, @RequestBody String userId) {
        return chatRoomService.deleteChatRoom(roomId, userId)
                .doOnSuccess(unused -> log.info("채팅방 {} 이 삭제되었습니다", roomId))
                .doOnError(error -> log.error("채팅방 삭제 실패: {}", error.getMessage()));
    }
}
