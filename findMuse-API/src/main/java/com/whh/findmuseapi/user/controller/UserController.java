package com.whh.findmuseapi.user.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.user.dto.request.UserProfile;
import com.whh.findmuseapi.user.dto.request.UserProfileTasteRequest;
import com.whh.findmuseapi.user.dto.response.NicknameDuplicationResponse;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "온보딩 : 닉네임 설정")
    @PostMapping("/profile/nickname")
    public ApiResponse<?> registerProfileNickname(@AuthenticationPrincipal User user,
                                                  @RequestBody UserProfile.NicknameRequest nicknameRequest) {
        userService.registerProfileNickname(user, nicknameRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @Operation(summary = "온보딩 : 닉네임 검사")
    @GetMapping("/profile/nickname")
    public ApiResponse<?> checkNicknameDuplication(@AuthenticationPrincipal User user,
                                                   @RequestBody UserProfile.NicknameRequest nicknameRequest) {
        NicknameDuplicationResponse nicknameDuplicationResponse = userService.checkNicknameDuplication(user, nicknameRequest);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, nicknameDuplicationResponse);
    }

    @Operation(summary = "온보딩 : 사용자 정보 설정")
    @PostMapping("/profile/information")
    public ApiResponse<?> registerProfileInformation(@AuthenticationPrincipal User user,
                                                     @RequestBody UserProfile.InformationRequest informationRequest) {
        userService.registerProfileInformation(user, informationRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @Operation(summary = "온보딩 : 사용자 위치 설정")
    @PostMapping("/profile/location")
    public ApiResponse<?> registerProfileLocation(@AuthenticationPrincipal User user,
                                                  @RequestBody UserProfile.LocationRequest locationRequest) {
        userService.registerProfileLocation(user, locationRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @Operation(summary = "온보딩 : 사용자 취향 설정")
    @PostMapping("/profile/taste")
    public ApiResponse<?> registerProfileTaste(@AuthenticationPrincipal User user,
                                               @RequestBody UserProfileTasteRequest userProfileTasteRequest) {
        userService.registerProfileTaste(user, userProfileTasteRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @Operation(summary = "온보딩 : 사용자 취향 수정")
    @PutMapping("/profile/taste")
    public ApiResponse<?> updateProfileTaste(@AuthenticationPrincipal User user,
                                             @RequestBody UserProfileTasteRequest userProfileTasteRequest) {
        userService.updateProfileTaste(user, userProfileTasteRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }
}
