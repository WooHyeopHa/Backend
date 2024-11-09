package com.whh.findmusechatting.chat.entity;

import lombok.Builder;

@Builder
public record Participant(
        String id,
        boolean isNotificationEnabled
) {
    public static Participant getNewParticipant(String id) {
        return Participant.builder().id(id).build();
    }
}
