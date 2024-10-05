package com.whh.findmusechatting.chat.entity;

import com.whh.findmusechatting.common.constant.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatMessage {
    @Id
    private String id;
    private String chatId;
    private String senderId;
    private String receiverId;
    private String senderName;
    private String receiverName;
    private String content;
    private Date timestamp;
    private MessageStatus status;
}
