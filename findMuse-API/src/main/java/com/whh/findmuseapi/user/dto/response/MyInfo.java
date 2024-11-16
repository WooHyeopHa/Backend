package com.whh.findmuseapi.user.dto.response;

import lombok.Builder;

@Builder
public record MyInfo(
        UserSimpleResponse userSimpleResponse,
        UserActivityResponse userActivityResponse,
        UserLevelResponse userLevelResponse,
        EventResponse eventResponse

) {
    @Builder
    public record UserSimpleResponse(
            String profileImageUrl,
            String nickname,
            int birthYear,
            String location,
            String comment
    ) {}

    @Builder
    public record UserActivityResponse(
            long wantToSee,
            int hasViewed,
            int eventReview
    ) {}

    @Builder
    public record UserLevelResponse(
            double percentage,
            int level,
            int artCount,
            int museCount,
            String description
    ) {}

    @Builder
    public record EventResponse(
            Long artCount,
            int participatedCount
    ) {}
}
