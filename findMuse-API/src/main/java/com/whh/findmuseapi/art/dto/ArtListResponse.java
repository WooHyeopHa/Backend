package com.whh.findmuseapi.art.dto;


import com.whh.findmuseapi.art.entity.Art;
import lombok.AllArgsConstructor;
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

    @Builder
    @Getter
    private static class ArtTumbnailResponse {
        private String title;
        private String place;
        private String genre;
        private String poster;
        private String startDate;
        private String endDate;
        private boolean isLiked;    // 관심여부

        private static ArtTumbnailResponse toDto(Art art) {
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
}
