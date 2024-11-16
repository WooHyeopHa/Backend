package com.whh.findmuseapi.art.dto.response;

import com.whh.findmuseapi.art.entity.ArtHistory;
import lombok.Builder;

@Builder
public record ArtHistoryResponse(
        Long id,
        float star,
        boolean activeStatus,
        Long userId,
        ArtTumbnailResponse art
) {
    public static ArtHistoryResponse fromEntity(ArtHistory artHistory) {
        return ArtHistoryResponse.builder()
                .id(artHistory.getId())
                .star(artHistory.getStar())
                .activeStatus(artHistory.isActiveStatus())
                .userId(artHistory.getUser().getId())
                .art(ArtTumbnailResponse.toDto(artHistory.getArt()))
                .build();
    }
}


