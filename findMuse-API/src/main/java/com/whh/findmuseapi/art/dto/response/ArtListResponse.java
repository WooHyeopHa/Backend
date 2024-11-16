package com.whh.findmuseapi.art.dto.response;


import com.whh.findmuseapi.art.entity.Art;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Builder
@Getter
public class ArtListResponse {

    private List<ArtTumbnailResponse> artList;

    public static ArtListResponse toDto(List<Art> arts) {
        return ArtListResponse.builder()
                .artList(arts.stream()
                        .map(ArtTumbnailResponse::toDto).toList()).build();
    }
}
