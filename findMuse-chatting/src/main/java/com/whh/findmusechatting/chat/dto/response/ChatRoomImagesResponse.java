package com.whh.findmusechatting.chat.dto.response;

import lombok.Builder;

import java.util.List;

public record ChatRoomImagesResponse(
        List<ImageMessageResponse> images,
        long totalCount
) {
    @Builder
    public ChatRoomImagesResponse {}
}
