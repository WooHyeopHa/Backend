package com.whh.findmuseapi.user.dto.response;

import lombok.Builder;

@Builder
public record NicknameDuplicationResponse(
        boolean isDuplicated
) {
}
