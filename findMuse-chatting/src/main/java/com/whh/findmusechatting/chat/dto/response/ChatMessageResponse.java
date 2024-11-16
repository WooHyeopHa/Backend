package com.whh.findmusechatting.chat.dto.response;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResponse(
        String id,
        String roomId,
        String senderId,
        String content,
        MessageType messageType,
        LocalDateTime timestamp,
        UserInfo userInfo,
        ChatMessage.ImageDetails imageDetails,
        ChatMessage.AppointmentDetails appointmentDetails
) {
    public record UserInfo(String ninkname, String profileImageUrl) {
        public static UserInfo getSystemInfo() {
            return new UserInfo(null, null);
        }
    }

    public static ChatMessageResponse from(ChatMessage message, UserInfo userInfo) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .timestamp(message.getTimestamp())
                .userInfo(userInfo)
                .imageDetails(message.getImageDetails())
                .appointmentDetails(message.getAppointmentDetails())
                .build();
    }
}
