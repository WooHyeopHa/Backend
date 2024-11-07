package com.whh.findmusechatting.chat.entity;

import com.whh.findmusechatting.chat.entity.constant.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notifications")
public class ChatNotification {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String roomId;
    private LocalDateTime timestamp;
    private NotificationType type;
}
