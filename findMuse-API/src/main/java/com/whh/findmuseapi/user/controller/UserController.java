package com.whh.findmuseapi.user.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.user.dto.request.UserProfile;
import com.whh.findmuseapi.user.dto.request.UserProfileTasteRequest;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
//
//    @PostMapping("/profile/nickname")
//    public ApiResponse<?> registerProfileNickname(@AuthenticationPrincipal User user,
//                                                  @RequestBody UserProfile.NicknameRequest nicknameRequest) {
//        userService.
//        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
//    }
    @PostMapping("/profile/information")
    public ApiResponse<?> registerProfileInformation(@AuthenticationPrincipal User user,
                                                     @RequestBody UserProfile.InformationRequest informationRequest) {
        userService.registerProfileInformation(user, informationRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @PostMapping("/profile/location")
    public ApiResponse<?> registerProfileLocation(@AuthenticationPrincipal User user,
                                                  @RequestBody UserProfile.LocationRequest locationRequest) {
        userService.registerProfileLocation(user, locationRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @PostMapping("/profile/taste")
    public ApiResponse<?> registerProfileTaste(@AuthenticationPrincipal User user,
                                               @RequestBody UserProfileTasteRequest userProfileTasteRequest) {
        userService.registerProfileTaste(user, userProfileTasteRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    @PutMapping("/profile/taste")
    public ApiResponse<?> updateProfileTaste(@AuthenticationPrincipal User user,
                                             @RequestBody UserProfileTasteRequest userProfileTasteRequest) {
        userService.updateProfileTaste(user, userProfileTasteRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }
}
