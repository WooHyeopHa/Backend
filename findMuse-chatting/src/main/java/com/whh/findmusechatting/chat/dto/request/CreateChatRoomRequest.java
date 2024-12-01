package com.whh.findmusechatting.chat.dto.request;

public record CreateChatRoomRequest(
    String name,
    String ownerId,
    String artId,
    String thumbnail
) {}
