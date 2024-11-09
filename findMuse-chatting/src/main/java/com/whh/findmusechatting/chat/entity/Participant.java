package com.whh.findmusechatting.chat.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Participant {
    private String id;
    private boolean isNotificationEnabled;

    public static Participant getNewParticipant(String id) {
        return Participant.builder().id(id).build();
    }

    public void setNotificationEnabled(boolean isNotificationEnabled) {
        this.isNotificationEnabled = isNotificationEnabled;
    }
}
