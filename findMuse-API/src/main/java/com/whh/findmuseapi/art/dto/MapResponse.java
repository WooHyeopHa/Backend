package com.whh.findmuseapi.art.dto;

import com.whh.findmuseapi.art.entity.Art;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public class MapResponse {
    List<MapByArtResponse> artMapList;

    public static MapResponse toDto(List<Art> artList) {
        return MapResponse.builder()
                .artMapList(artList.stream().
                        map(MapByArtResponse::toDto).collect(Collectors.toList())).build();
    }

    @Builder
    private static class MapByArtResponse {
        private String latitude;
        private String longitude;
        private String poster;
        private String title;
        private String genre;
        private String place;
        private String startDate;
        private String endDate;

        private static MapByArtResponse toDto(Art art){
            return MapByArtResponse.builder()
                    .latitude(art.getLatitude())
                    .longitude(art.getLongitude())
                    .title(art.getTitle())
                    .genre(art.getArtType().getInfo())
                    .poster(art.getPoster())
                    .startDate(art.getStartDate())
                    .endDate(art.getEndDate())
                    .place(art.getPlace()).build();
        }
    }
}
