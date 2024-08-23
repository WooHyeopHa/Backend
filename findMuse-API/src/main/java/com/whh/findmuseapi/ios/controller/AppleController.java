package com.whh.findmuseapi.ios.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.ios.dto.AppleLoginResponse;
import com.whh.findmuseapi.ios.service.AppleService;
import com.whh.findmuseapi.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
    public ApiResponse<?> callback(AppleLoginResponse appleLoginResponse) throws IOException {
        // 로그 추가 (디버깅 용도)
        System.out.println(appleLoginResponse.toString());
        
        User user = appleService.login(appleLoginResponse);
        
        // 로그인 성공
        if (user != null) {
            return ApiResponse.createSuccess(ResponseCode.CREATED, user);
        }
        return ApiResponse.createError(ResponseCode.INTERNAL_SERVER_ERROR, "전역 에러 처리 전 임시 메시지");
    }
    
    @PostMapping("/token")
    public ApiResponse<?> loginWithIdentityToken(HttpServletResponse response, @RequestBody AppleLoginResponse appleLoginResponse) throws BadRequestException {
        User user = appleService.loginWithToken(appleLoginResponse);
        
        if (user != null) {
            appleService.loginSuccess(user, response);
            return ApiResponse.createSuccess(ResponseCode.SUCCESS, user);
        }
        return ApiResponse.createError(ResponseCode.INTERNAL_SERVER_ERROR, "전역 에러 처리 전 임시 메시지");
    }
    
    @DeleteMapping("/revoke")
    public ApiResponse<?> revokeAppleAccount(Long userId) throws BadRequestException{
        appleService.deleteAppleAccount(userId);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }
}
