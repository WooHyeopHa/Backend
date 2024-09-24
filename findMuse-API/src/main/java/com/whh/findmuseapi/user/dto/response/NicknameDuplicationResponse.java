package com.whh.findmuseapi.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NicknameDuplicationResponse {
    boolean isDuplicated;
}
