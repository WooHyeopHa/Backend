package com.whh.findmuseapi.ios.dto;

import com.whh.findmuseapi.ios.dto.user.AppleUser;

public record AppleAuthRequest(String state,
                               String authorizationCode,
                               String identityToken,
                               AppleUser user) {
}

