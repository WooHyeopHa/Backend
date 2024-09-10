package com.whh.findmuseapi.post.service;

import com.whh.findmuseapi.post.entity.Post;
import com.whh.findmuseapi.user.entity.User;

/**
 * class: VolunteerService.
 *
 * @author devminseo
 * @version 8/24/24
 */
public interface VolunteerService {

    /**
     * 모집글에 참여신청이 완료된 회원 개수를 가져옵니다.
     *
     * @param post 모집글.
     * @return 회원수
     */
    Long getInvitedCount(Post post);

    /**
     * 모집글 지원 여부 판단.
     *
     * @param user 대상 회원
     * @param post 대상 모집글
     * @return 지원 여부
     */
    boolean volunteerCheck(User user, Post post);

    
}
