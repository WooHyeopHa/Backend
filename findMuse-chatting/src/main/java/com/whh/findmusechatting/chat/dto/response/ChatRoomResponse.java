package com.whh.findmusechatting.chat.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
public class ChatRoomResponse {
    private String id;
    private String name;
    private String thumbnail;
    private String lastMessage;
    private String lastMessageTime;
    private Integer unreadCount;
    private Integer participantCount;
    private List<String> participants;

    public static String formatLastMessageTime(LocalDateTime timestamp) {
        if (timestamp == null) return "";

        LocalDate today = LocalDate.now();
        if (timestamp.toLocalDate().equals(today)) {
            return timestamp.format(DateTimeFormatter.ofPattern("a h:mm")
                    .withLocale(java.util.Locale.KOREAN));
        } else {
            return timestamp.format(DateTimeFormatter.ofPattern("M월 d일"));
        }
    }
}
