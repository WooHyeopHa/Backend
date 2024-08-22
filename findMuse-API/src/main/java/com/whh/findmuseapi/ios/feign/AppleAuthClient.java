package com.whh.findmuseapi.ios.feign;

import com.whh.findmuseapi.ios.dto.AppleRevokeRequest;
import com.whh.findmuseapi.ios.dto.AppleToken;
import com.whh.findmuseapi.ios.dto.key.ApplePublicKeys;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "appleAuthClient", url = "https://appleid.apple.com/auth")
public interface AppleAuthClient {
    @GetMapping(value = "/keys")
    ApplePublicKeys getAppleAuthPublicKey();
    
    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    AppleToken.Response getToken(AppleToken.Request request);
    
    @PostMapping(value = "/revoke", consumes = "application/x-www-form-urlencoded")
    String revoke(AppleRevokeRequest request);
}
