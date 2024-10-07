package com.whh.findmuseapi.jwt.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.jwt.dto.RefreshTokenResponse;
import com.whh.findmuseapi.jwt.service.JwtService;
import com.whh.findmuseapi.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt")
public class JwtController {

    private final JwtService jwtService;

    @PatchMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> reIssueRefreshToken(@AuthenticationPrincipal User user) {
        String refreshToken = jwtService.reIssueRefreshToken(user);
        return ApiResponse.createSuccess(ResponseCode.RESOURCE_CREATED, new RefreshTokenResponse(refreshToken));
    }

}
