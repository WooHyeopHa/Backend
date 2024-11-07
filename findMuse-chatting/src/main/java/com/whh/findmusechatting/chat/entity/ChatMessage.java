package com.whh.findmusechatting.chat.entity;

import com.whh.findmusechatting.chat.dto.request.CreateChatMessageRequest;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "messages")
public class ChatMessage {
    @Id
    private String id;
    private String roomId;
    private String senderId;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;

    public static ChatMessage of(CreateChatMessageRequest request) {
        return ChatMessage.builder()
                .roomId(request.roomId())
                .senderId(request.senderId())
                .content(request.content())
                .messageType(MessageType.CHAT)
                .timestamp(LocalDateTime.now())
                .build();
    }
}