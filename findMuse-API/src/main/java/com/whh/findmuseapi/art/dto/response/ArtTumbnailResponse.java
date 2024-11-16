package com.whh.findmuseapi.art.dto.response;

import com.whh.findmuseapi.art.entity.Art;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
class ArtTumbnailResponse {
    private String title;
    private String place;
    private String genre;
    private String poster;
    private String startDate;
    private String endDate;
    private boolean isLiked;    // 관심여부

    public static ArtTumbnailResponse toDto(Art art) {
        return ArtTumbnailResponse.builder()
                .title(art.getTitle())
                .place(art.getPlace())
                .genre(art.getArtType().getInfo())
                .poster(art.getPoster())
                .startDate(art.getStartDate())
                .endDate(art.getEndDate())
                .isLiked(!art.getArtLikes().isEmpty())
                .build();
    }
}
