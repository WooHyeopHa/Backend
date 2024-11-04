package com.whh.findmusechatting.chat.entity;

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
@Document(collection = "messages")
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    @Id
    private String id;
    private String roomId;
    private String senderId;
    private String senderName;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;
}