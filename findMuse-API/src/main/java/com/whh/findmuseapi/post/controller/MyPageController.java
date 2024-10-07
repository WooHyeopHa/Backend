package com.whh.findmuseapi.post.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.post.dto.response.VolunteerMyPageListResponse;
import com.whh.findmuseapi.post.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * class: MyPageController.
 *
 * @author devminseo
 * @version 9/5/24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final VolunteerService volunteerService;

    /**
     * 마이페이지에서 자신의 지원 내역을 확인하는 엔드포인트
     *
     * @param userId 유저 ID
     * @return 마이페이지 신청 내역
     */
    @GetMapping("/{userId}")
    public ApiResponse<?> getMyPageVolunteerList(@PathVariable Long userId) {
        VolunteerMyPageListResponse response = volunteerService.getMyPageVolunteerList(userId);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, response);
    }

}
