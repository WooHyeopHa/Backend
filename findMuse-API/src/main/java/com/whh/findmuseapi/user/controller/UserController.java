package com.whh.findmuseapi.user.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.user.dto.request.UserProfile;
import com.whh.findmuseapi.user.dto.request.UserProfileInformationRequest;
import com.whh.findmuseapi.user.dto.request.UserProfileTasteRequest;
import com.whh.findmuseapi.user.dto.response.NicknameDuplicationResponse;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
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
        log.info("NickName 설정 완료 : {}", user.getNickname());
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @Operation(summary = "온보딩 : 닉네임 검사")
    @GetMapping("/profile/nickname")
    public ApiResponse<?> checkNicknameDuplication(@RequestParam String nickname) {
        NicknameDuplicationResponse nicknameDuplicationResponse = userService.checkNicknameDuplication(nickname);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, nicknameDuplicationResponse);
    }

    @Operation(summary = "온보딩 : 사용자 정보 설정")
    @PostMapping(value = "/profile/information", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> registerProfileInformation(@AuthenticationPrincipal User user,
                                                     @ModelAttribute("informationRequest") UserProfileInformationRequest informationRequest,
                                                     @RequestPart("profileImage") MultipartFile profileImage) throws IOException {
        log.info(informationRequest.toString());
        userService.registerProfileInformation(user, informationRequest, profileImage);
        log.info("사용자 정보 설정 완료 : {}, {}, {}", user.getBirthYear(), user.getGender().getInfo(), user.getProfileImageUrl());
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @Operation(summary = "온보딩 : 사용자 위치 설정")
    @PostMapping("/profile/location")
    public ApiResponse<?> registerProfileLocation(@AuthenticationPrincipal User user,
                                                  @RequestBody UserProfile.LocationRequest locationRequest) {
        userService.registerProfileLocation(user, locationRequest);
        log.info("사용자 위치 설정 완료 : {}", user.getLocation());
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @Operation(summary = "온보딩 : 사용자 취향 설정")
    @PostMapping("/profile/taste")
    public ApiResponse<?> registerProfileTaste(@AuthenticationPrincipal User user,
                                               @RequestBody UserProfileTasteRequest userProfileTasteRequest) {
        userService.registerProfileTaste(user.getId(), userProfileTasteRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @Operation(summary = "온보딩 : 사용자 취향 수정")
    @PutMapping("/profile/taste")
    public ApiResponse<?> updateProfileTaste(@AuthenticationPrincipal User user,
                                             @RequestBody UserProfileTasteRequest userProfileTasteRequest) {
        userService.updateProfileTaste(user.getId(), userProfileTasteRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }
}