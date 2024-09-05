package com.whh.findmuseapi.post.dto.response;

import com.whh.findmuseapi.post.entity.Post;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * class: PostReadResponse.
 *
 * @author devminseo
 * @version 8/21/24
 */
@Getter
@Builder
public class PostReadResponse {
    private Long id;
    private String title;
    private String content;
    private String place;
    private LocalDate createDate;
    private LocalDate endDate;
    private int dDay;
    private int inviteCount;
    private int invitedCount;
    private int viewCount;
    private String ages; //선호 연령
    private String artName;
    private List<String> tagList;
    private boolean isWriter;


    public static PostReadResponse toDto(Post post,int invitedCount,boolean isWriter) {
        return PostReadResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .place(post.getPlace())
                .createDate(post.getCreateDate())
                .endDate(post.getEndDate())
                .dDay(calculateDDay(post.getEndDate()))
                .inviteCount(post.getInviteCount())
                .invitedCount(invitedCount)
                .viewCount(post.getViewCount())
                .ages(post.getAges().name())
                .artName(post.getArt().getTitle())
                .tagList(post.getTagList().stream()
                        .map(tag -> tag.getTag().getName())
                        .toList())
                .isWriter(isWriter)
                .build();
    }

    private static int calculateDDay(LocalDate endDate) {
        LocalDate now = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(now, endDate);
    }
}
