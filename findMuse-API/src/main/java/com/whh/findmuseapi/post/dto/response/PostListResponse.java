package com.whh.findmuseapi.post.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * class: PostListResponse.
 *
 * @author devminseo
 * @version 9/3/24
 */

@Getter
@Builder
public class PostListResponse {
    private List<PostListReadResponse> postList;

    public static PostListResponse toDto(List<PostListReadResponse> postList) {
        return PostListResponse.builder()
                .postList(postList)
                .build();
    }
}
