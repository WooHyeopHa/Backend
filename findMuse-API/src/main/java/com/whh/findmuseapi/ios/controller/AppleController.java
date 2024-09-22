package com.whh.findmuseapi.ios.controller;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.ios.dto.AppleLoginResponse;
import com.whh.findmuseapi.ios.service.AppleService;
import com.whh.findmuseapi.jwt.service.JwtService;
import com.whh.findmuseapi.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/apple")
public class AppleController {
    
    private final AppleService appleService;
    private final JwtService jwtService;
    
    @PostMapping("/token")
    public ApiResponse<?> loginWithIdentityToken(HttpServletResponse response, @RequestBody AppleLoginResponse appleLoginResponse) {
        User user = appleService.loginWithToken(appleLoginResponse);
        appleService.loginSuccess(user, response);
        
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, user);
    }
    
    @DeleteMapping("/revoke")
    public ApiResponse<?> revokeAppleAccount(@AuthenticationPrincipal User user,
                                             @RequestParam String code) {
        appleService.deleteAppleAccount(user, code);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }

    /**
     * Apple Login은 idToken이 없어서 못하니, test용도로 token 생성 후 반환하는 API
     */
    @GetMapping("/test")
    public ApiResponse<?> testLogin(HttpServletResponse response) {
        String acessToekn = jwtService.createAccessToken("eee@email.com");
        String refreshToken = jwtService.createRefreshToken();
        jwtService.sendAccessAndRefreshToken(response, acessToekn, refreshToken);

        return ApiResponse.createSuccessWithNoContent(ResponseCode.RESOURCE_CREATED);
    }
}
