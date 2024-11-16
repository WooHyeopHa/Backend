package com.whh.findmuseapi.user.util;

import com.whh.findmuseapi.art.entity.ArtHistory;
import com.whh.findmuseapi.art.entity.ArtLike;
import com.whh.findmuseapi.common.constant.MuseLevel;
import com.whh.findmuseapi.user.dto.response.MyInfo;
import com.whh.findmuseapi.user.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserMapper {

    public static MyInfo toMyInfo(User user, Long artCount, List<ArtLike> artLikes, List<ArtHistory> histories) {
        return MyInfo.builder()
                .userSimpleResponse(toUserSimpleResponse(user))
                .userActivityResponse(toUserActivityResponse(user, artLikes, histories))
                .userLevelResponse(toMyLevelResponse(user))
                .eventResponse(toEventResponse(user, artCount))
                .build();
    }

    public static MyInfo.UserSimpleResponse toUserSimpleResponse(User user) {
        return MyInfo.UserSimpleResponse.builder()
                .profileImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .location(user.getLocation())
                .birthYear(user.getBirthYear())
                .comment(user.getComment())
                .build();
    }

    public static MyInfo.UserActivityResponse toUserActivityResponse(User user, List<ArtLike> artLikes, List<ArtHistory> histories) {
        return MyInfo.UserActivityResponse.builder()
                .eventReview(user.getArtReviews().size())
                .hasViewed(histories.size()) // histories의 크기를 사용
                .wantToSee(artLikes.size())   // artLikes의 크기를 사용
                .build();
    }

    public static MyInfo.UserLevelResponse toMyLevelResponse(User user) {
        MuseLevel museLevel = MuseLevel.determineLevel(user.getArtCount(), user.getFindMuseCount());
        int denominator = museLevel.getFindMuseCount() + museLevel.getViewCount();
        int numerator = Math.min(user.getArtCount(), museLevel.getViewCount())
                + Math.min(user.getFindMuseCount(), museLevel.getFindMuseCount());

        double percentage = ((double) numerator / denominator) * 100;

        return MyInfo.UserLevelResponse.builder()
                .artCount(user.getArtCount())
                .museCount(user.getFindMuseCount())
                .level(museLevel.getLevel())
                .description(museLevel.getDescription())
                .percentage(percentage)
                .build();
    }

    public static MyInfo.EventResponse toEventResponse(User user, Long artCount) {
        return MyInfo.EventResponse.builder()
                .artCount(artCount)
                .participatedCount(user.getArtCount() + user.getUserReviews().size())
                .build();
    }
}
