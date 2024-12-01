package com.whh.findmusechatting.chat.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user_room_status")
public class UserRoomStatus {
    @Id
    private String id;
    private String userId;
    private String roomId;
    private LocalDateTime lastReadTimestamp;
}