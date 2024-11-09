package com.whh.findmusechatting.chat.controller;

import com.whh.findmusechatting.chat.dto.response.ChatRoomImagesResponse;
import com.whh.findmusechatting.chat.service.ChatImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/chat/images")
@RequiredArgsConstructor
public class ChatImageController {
    private final ChatImageService chatImageService;

    @Operation(summary = "채팅방의 모든 이미지 메시지 조회")
    @GetMapping("/{roomId}")
    public Mono<ChatRoomImagesResponse> getRoomImages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return chatImageService.getChatRoomImages(roomId, page, size);
    }

    @GetMapping("/download/{messageId}")
    @Operation(summary = "이미지 메시지 다운로드")
    public Mono<Void> downloadImage(
            @PathVariable String messageId,
            ServerHttpResponse response
    ) {
        return chatImageService.downloadChatImage(messageId, response);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "단일 이미지 메시지 삭제")
    public Mono<Void> deleteImage(@PathVariable String messageId) {
        return chatImageService.deleteImageMessage(messageId);
    }

    @DeleteMapping("/room/{roomId}")
    @Operation(summary = "채팅방의 모든 이미지 메시지 삭제")
    public Mono<Void> deleteAllRoomImages(@PathVariable String roomId) {
        return chatImageService.deleteAllRoomImages(roomId);
    }
}