package com.whh.findmuseapi.post.service;

import com.whh.findmuseapi.post.dto.request.PostCreateRequest;

/**
 * class: PostService.
 * 게시글 관련 서비스 입니다.
 *
 * @author devminseo
 * @version 8/20/24
 */

public interface PostService {


    /**
     * 게시물 생성 로직입니다.
     *
     * @param createRequest 게시물 생성 정보
     */
    void createPost(PostCreateRequest createRequest);
}
