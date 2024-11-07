package com.whh.findmusechatting.chat.dto.request;

public record CreateChatMessageRequest(
        String roomId,
        String senderId,
        String content
) {
}
