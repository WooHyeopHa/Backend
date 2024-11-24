package com.whh.findmuseapi.review.dto;

import com.whh.findmuseapi.review.entity.ArtReview;
import com.whh.findmuseapi.review.entity.ArtReviewLike;
import com.whh.findmuseapi.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@Getter
public class AllReviewResponse {

    private int reviewCnt;
    private float totalStar;
    private List<SingleReviewResponse> reviewList;


    public static AllReviewResponse toDto(float star, List<ArtReview> reviews) {
        return AllReviewResponse.builder()
                .reviewCnt(reviews.size())
                .totalStar(star)
                .reviewList(SingleReviewResponse.toDto(reviews)).build();
    }

    @Builder
    @Getter
    private static class SingleReviewResponse {
        private Long reviewId;
        private String name;
        private String userPhoto;
        private String star;
        private String content;
        private String date;
        private boolean isThumbed;
        private int thumbsUpCnt;

        private static List<SingleReviewResponse> toDto(List<ArtReview> reviews) {
            return reviews.stream()
                    .map(r -> {
                        User writeUser = r.getUser();
                        return SingleReviewResponse.builder()
                                .reviewId(r.getId())
                                .name(writeUser.getNickname())
                                .userPhoto(writeUser.getProfileImageUrl())
                                .star(r.getStar())
                                .content(r.getContent())
                                .date(r.getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                .isThumbed(!r.getReviewLikes().isEmpty())
                                .thumbsUpCnt(r.getReviewLikes().size()).build();
                    }).toList();
        }
    }
}
