package com.whh.findmuseapi.post.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.post.dto.response.VolunteerPostListResponse;
import com.whh.findmuseapi.post.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * class: VolunteerController.
 * 모집글 지워자에 대한 컨트롤러입니다.
 *
 * @author devminseo
 * @version 8/31/24
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/volunteer")
public class VolunteerController {
    private final VolunteerService volunteerService;

    /**
     * 모집글에 지원을 신청하는 엔드포인트
     *
     * @param postId 모집글 ID
     * @param userId 지원자 ID
     * @return 지원 결과
     */
    @PostMapping("/apply")
    public ApiResponse<?> applyVolunteer(@RequestParam Long postId, @RequestParam Long userId) {
        volunteerService.applyVolunteer(postId, userId);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    /**
     * 모집글에 지원 취소하는 엔드포인트
     *
     * @param volunteerId 지원자 ID
     * @param userId 모집글 작성자 ID
     * @return 취소 결과
     */
    @DeleteMapping("/cancel")
    public ApiResponse<?> cancelVolunteer(@RequestParam Long volunteerId, @RequestParam Long userId) {
        volunteerService.cancelVolunteer(volunteerId, userId);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    /**
     * 모집글 지원자 목록 조회 엔드포인트 (참여자와 대기자)
     *
     * @param postId 모집글 ID
     * @param userId 작성자 ID
     * @return 지원자 목록
     */
    @GetMapping("/post/{postId}/volunteers")
    public ApiResponse<?> getPostVolunteerList(@PathVariable Long postId, @RequestParam Long userId) {
        VolunteerPostListResponse response = volunteerService.getPostVolunteerList(postId, userId);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, response);
    }


    /**
     * 모집글의 지원자를 수락하는 엔드포인트
     *
     * @param postId 모집글 ID
     * @param writerId 작성자 ID
     * @param targetId 지원자 ID
     * @return 수락 결과
     */
    @PostMapping("/{postId}/accept")
    public ApiResponse<?> acceptVolunteer(@PathVariable Long postId, @RequestParam Long writerId, @RequestParam Long targetId) {
        volunteerService.acceptVolunteer(postId, writerId, targetId);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    /**
     * 모집글의 지원자를 거절하는 엔드포인트
     *
     * @param postId 모집글 ID
     * @param writerId 작성자 ID
     * @param targetId 지원자 ID
     * @return 거절 결과
     */
    @PostMapping("/{postId}/refuse")
    public ApiResponse<?> refuseVolunteer(@PathVariable Long postId, @RequestParam Long writerId, @RequestParam Long targetId) {
        volunteerService.refusalVolunteer(postId, writerId, targetId);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

}
