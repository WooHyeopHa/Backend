package com.whh.findmuseapi.post.dto.response;

import static com.whh.findmuseapi.post.dto.response.PostOneReadResponse.calculateDDay;

import com.whh.findmuseapi.post.entity.Post;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * class: PostListReadResponse.
 *
 * @author devminseo
 * @version 9/3/24
 */
@Getter
@Builder
public class PostListReadResponse {
    private Long id;
    private String title;
    private String content;
    private String place;
    private LocalDate createDate;
    private LocalDate endDate;
    private int dDay;
    private int inviteCount;
    private int viewCount;
    private String ages; //선호 연령
    private String artName;
    private List<String> tagList;

    public static PostListReadResponse toDto(Post post) {
        return PostListReadResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .place(post.getPlace())
                .createDate(post.getCreateDate())
                .endDate(post.getEndDate())
                .dDay(calculateDDay(post.getEndDate()))
                .inviteCount(post.getInviteCount())
                .viewCount(post.getViewCount())
                .ages(post.getAges().name())
                .artName(post.getArt().getTitle())
                .tagList(post.getTagList().stream()
                        .map(tag -> tag.getTag().getName())
                        .toList())
                .build();
    }
}
