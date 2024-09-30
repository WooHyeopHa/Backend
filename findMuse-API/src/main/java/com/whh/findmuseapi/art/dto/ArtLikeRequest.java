package com.whh.findmuseapi.art.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArtLikeRequest {
    private Long artId;
    private Long userId;
}
