package com.whh.findmuseapi.art.dto;

import com.whh.findmuseapi.art.entity.Art;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Getter
public class ArtHomeResponse {

    private List<ArtRandomResponse> artRandomList;
    private List<ArtRankResponse> artRankList;
    private List<ArtSimpleResponse> artNewOpenList;

    public static ArtHomeResponse toDto(List<ArtRandomResponse> artRandList, List<ArtRankResponse> artRankList,List<Art> artNewOpen) {
        return ArtHomeResponse.builder()
                .artRandomList(artRandList)
                .artRankList(artRankList)
                .artNewOpenList(artNewOpen.stream()
                                .map(ArtSimpleResponse::toDto).toList()).build();
    }

    @Builder
    @Getter
    public static class ArtRankResponse {
        private String genre;
        private List<ArtSimpleResponse> artList;

        public static ArtRankResponse toDto(String genre, List<Art> artList) {
            return ArtRankResponse.builder()
                    .genre(genre)
                    .artList(artList.stream()
                            .map(ArtSimpleResponse::toDto)
                            .toList()).build();
        }
    }

    @Builder
    @Getter
    private static class ArtSimpleResponse {
        private String title;
        private String genre;
        private String poster;
        private String startDate;
        private String endDate;
        private boolean isLiked;    // 관심여부

        private static ArtSimpleResponse toDto(Art art) {
            return ArtSimpleResponse.builder()
                    .title(art.getTitle())
                    .genre(art.getArtType().getInfo())
                    .poster(art.getPoster())
                    .startDate(art.getStartDate())
                    .endDate(art.getEndDate())
                    .isLiked(!art.getArtLikes().isEmpty())
                    .build();
        }
    }
}
