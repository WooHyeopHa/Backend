package com.whh.findmuseapi.post.service;

import com.whh.findmuseapi.post.dto.response.VolunteerMyPageListResponse;
import com.whh.findmuseapi.post.dto.response.VolunteerPostListResponse;
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

    /**
     * 모집글 글쓴이가 신청자 목록을 들어갈때 띄우는 신청자 리스트
     * 참여자, 대기자 2가지 종류의 리스트로 반환.
     *
     * @param postId 해당 모집글 번호
     * @return 참여자, 대기자 리스트
     */
    VolunteerPostListResponse getPostVolunteerList(Long postId, Long userId);

    /**
     * 마이페이지에서 자신의 모든 신청 내역을 확인하는 리스트.
     *
     * @param userId 유저 아이디
     * @return 승인, 대기, 거절 리스트
     */
    VolunteerMyPageListResponse getMyPageVolunteerList(Long userId);
}
