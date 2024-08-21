package com.whh.findmuseapi.post.dto.response;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.post.entity.PostTag;
import com.whh.findmuseapi.post.entity.Volunteer;
import com.whh.findmuseapi.user.entity.User;
import java.time.LocalDate;
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
    private int inviteCount;
    private int viewCount;
    private String ages; //선호 연령
    private String artName;
    private Long userId;
    private List<Long> volunteeredList;
    private List<String> tagList;
}
