package com.whh.findmuseapi.art.dto;

import com.whh.findmuseapi.art.entity.Art;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class ArtOneResponse {
    private String poster;
    private String title;
    private String genre;
    private String age;
    private String place;
    private String startDate;
    private String endDate;
    private String startTime;
    private String sPark;
    private String park;
    private String detailPhoto;
    private float starScore;
    private int reviewCnt;


    public static ArtOneResponse toDto(Art art) {
        //TODO : 배경은 어떻게?
        String detialPhoto = "Empty";
        return ArtOneResponse.builder()
                .poster(art.getPoster())
                .title(art.getTitle())
                .genre(art.getArtType().getInfo())
                .age(art.getAge())
                .place(art.getPlace())
                .startDate(art.getStartDate())
                .endDate(art.getEndDate())
                .startTime(art.getStartTime())
                .sPark(art.getSPark())
                .park(art.getPark())
                .detailPhoto(detialPhoto).build();
    }
}
