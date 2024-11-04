package com.whh.findmusechatting.chat.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomParticipantResponse {
    private String userId;
}
