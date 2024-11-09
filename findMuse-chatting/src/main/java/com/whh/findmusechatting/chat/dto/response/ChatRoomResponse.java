package com.whh.findmusechatting.chat.dto.response;

import com.whh.findmusechatting.chat.entity.Participant;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Builder
public record ChatRoomResponse(
        String id,
        String name,
        String thumbnail,
        String lastMessage,
        String lastMessageTime,
        Integer unreadCount,
        Integer participantCount,
        List<Participant> participants
) {
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