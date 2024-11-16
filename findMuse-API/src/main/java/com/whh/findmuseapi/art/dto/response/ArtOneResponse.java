package com.whh.findmuseapi.art.dto.response;

import com.whh.findmuseapi.art.entity.Art;
import lombok.Builder;
import lombok.Getter;

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
    private UserInfo userInfo;


    public static ArtOneResponse toDto(Art art, boolean isLiked, boolean isStared, boolean isViewed) {
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
                .detailPhoto(detialPhoto)
                .userInfo(UserInfo.toDto(isLiked, isStared, isViewed)).build();
    }

    @Getter
    @Builder
    private static class UserInfo {
        private boolean isLiked;
        private boolean isStared;
        private boolean isReviewed;

        private static UserInfo toDto(boolean isLiked, boolean isStared, boolean isViewed) {
            return UserInfo.builder()
                    .isLiked(isLiked)
                    .isStared(isStared)
                    .isReviewed(isViewed).build();
        }
    }
}
