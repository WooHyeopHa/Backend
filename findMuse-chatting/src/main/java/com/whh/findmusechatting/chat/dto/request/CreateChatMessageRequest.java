package com.whh.findmusechatting.chat.dto.request;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import lombok.Builder;

public record CreateChatMessageRequest(
        String roomId,
        String senderId,
        String content,
        MessageType messageType,
        ChatMessage.ImageDetails imageDetails,
        ChatMessage.AppointmentDetails appointmentDetails
) {
    @Builder
    public CreateChatMessageRequest {}
}