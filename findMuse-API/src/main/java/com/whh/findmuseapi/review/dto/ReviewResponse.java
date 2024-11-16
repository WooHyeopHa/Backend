package com.whh.findmuseapi.review.dto;

import com.whh.findmuseapi.review.entity.ArtReview;
import com.whh.findmuseapi.review.entity.ArtReviewLike;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public record ReviewResponse(
        Long artId,
        String artTitle,
        String artPoster,
        String name,
        String star,
        String content,
        String date,
        boolean isThumbed,
        int thumbsUpCnt
) {
    public static ReviewResponse toDto(ArtReview review, ArtReviewLike reviewLike) {
        return ReviewResponse.builder()
                .artId(review.getArt().getId())
                .artTitle(review.getArt().getTitle())
                .artPoster(review.getArt().getPoster())
                .name(review.getUser().getNickname())
                .star(review.getStar())
                .content(review.getContent())
                .date(review.getCreateDate().toString())
                .date(review.getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .isThumbed(reviewLike != null && reviewLike.getUser() != null)
                .thumbsUpCnt(review.getLikeCount())
                .build();
    }
}
