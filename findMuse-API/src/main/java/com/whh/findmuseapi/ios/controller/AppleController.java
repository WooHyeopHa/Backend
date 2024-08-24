package com.whh.findmuseapi.ios.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.ios.dto.AppleLoginResponse;
import com.whh.findmuseapi.ios.service.AppleService;
import com.whh.findmuseapi.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/apple")
public class AppleController {
    
    private final AppleService appleService;
    
    @PostMapping("/callback")
    public ApiResponse<?> callback(HttpServletRequest request) {
        AppleLoginResponse appleLoginResponse = AppleLoginResponse.builder()
            .code(request.getParameter("code"))
            .idToken(request.getParameter("id_token"))
            .build();
        
        User user = appleService.login(appleLoginResponse);
        
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, user);
    }
    
    @PostMapping("/token")
    public ApiResponse<?> loginWithIdentityToken(HttpServletResponse response, @RequestBody AppleLoginResponse appleLoginResponse) {
        User user = appleService.loginWithToken(appleLoginResponse);
        appleService.loginSuccess(user, response);
        
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, user);
    }
    
    @DeleteMapping("/revoke")
    public ApiResponse<?> revokeAppleAccount(Long userId) {
        appleService.deleteAppleAccount(userId);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }
}
