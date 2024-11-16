package com.whh.findmuseapi.user.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.service.BlackUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class BlackUserController {

    private final BlackUserService blackUserService;

    @Operation(summary = "사용자 차단")
    @PostMapping("/block/{targetUserId}")
    public ApiResponse<Void> blockUser(@AuthenticationPrincipal User user, @PathVariable long targetUserId) {
        blackUserService.blockUser(user, targetUserId);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, null);
    }

    @Operation(summary = "차단된 사용자 목록 조회")
    @GetMapping("/list")
    public ApiResponse<List<User>> getBlockedUsers(@AuthenticationPrincipal User user) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, blackUserService.getBlockedUsers(user));
    }

    @Operation(summary = "사용자 차단 해제")
    @PostMapping("/unblock/{targetUserId}")
    public ApiResponse<Void> unblockUser(@AuthenticationPrincipal User user, @PathVariable long targetUserId) {
        blackUserService.unblockUser(user, targetUserId);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, null);
    }
}
