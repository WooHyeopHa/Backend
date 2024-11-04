package com.whh.findmusechatting.chat.entity;

import com.whh.findmusechatting.chat.dto.request.ChatRoomUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatrooms")
public class ChatRoom {
    @Id
    private String id;
    private String name;
    private String owner;
    private String thumbnail;
    private List<String> participants;
    private LocalDateTime createdAt;

    public void updateChatRoom(ChatRoomUpdateRequest chatRoomUpdateRequest) {
        if (chatRoomUpdateRequest.getName() != null) {
            this.name = chatRoomUpdateRequest.getName();
        }
        if (chatRoomUpdateRequest.getThumbnail() != null) {
            this.thumbnail = chatRoomUpdateRequest.getThumbnail();
        }
    }
}