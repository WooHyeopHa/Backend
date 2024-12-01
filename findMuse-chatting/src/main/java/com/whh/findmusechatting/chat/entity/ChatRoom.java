package com.whh.findmusechatting.chat.entity;

import com.whh.findmusechatting.chat.dto.request.UpdateChatRoomRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatrooms")
public class ChatRoom {
    @Id
    private String id;
    private String name;
    private String ownerId;
    private String artId;
    private String thumbnail;
    
    @Field("participants")
    private List<Participant> participants;
    private LocalDateTime createdAt;

    public void updateChatRoom(UpdateChatRoomRequest updateChatRoomRequest) {
        if (updateChatRoomRequest.name() != null) {
            this.name = updateChatRoomRequest.name();
        }
        if (updateChatRoomRequest.thumbnail() != null) {
            this.thumbnail = updateChatRoomRequest.thumbnail();
        }
    }
}