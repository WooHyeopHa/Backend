package com.whh.findmusechatting.chat.dto.response;

import com.whh.findmusechatting.chat.entity.ChatMessage;

import java.time.LocalDateTime;

public record ImageMessageResponse(
        String id,
        String senderId,
        String imageUrl,
        String originalFileName,
        String contentType,
        long fileSize,
        LocalDateTime timestamp,
        ChatMessageResponse.UserInfo senderInfo
) {
    public static ImageMessageResponse from(ChatMessage message, ChatMessageResponse.UserInfo userInfo) {
        return new ImageMessageResponse(
                message.getId(),
                message.getSenderId(),
                message.getContent(),
                message.getImageDetails().getOriginalFileName(),
                message.getImageDetails().getContentType(),
                message.getImageDetails().getFileSize(),
                message.getTimestamp(),
                userInfo
        );
    }
}
