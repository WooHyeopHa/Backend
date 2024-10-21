package com.whh.findmuseapi.art.dto;

import com.whh.findmuseapi.art.entity.Art;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ArtHomeResponse {

    private List<ArtRandomResponse> artRandomList;
    private ArtTodayRandResponse artTodayRandom;

    public static ArtHomeResponse toDto(List<ArtRandomResponse> artRandList, Art artTodayRand) {
        return ArtHomeResponse.builder()
                .artRandomList(artRandList)
                .artTodayRandom(ArtTodayRandResponse.toDto(artTodayRand)).build();
    }

    @Builder
    @Getter
    private static class ArtTodayRandResponse {
        private String title;
        private String startDate;
        private String endDate;

        private static ArtTodayRandResponse toDto(Art art) {
            return ArtTodayRandResponse.builder()
                    .title(art.getTitle())
                    .startDate(art.getStartDate())
                    .endDate(art.getEndDate())
                    .build();
        }
    }
}
