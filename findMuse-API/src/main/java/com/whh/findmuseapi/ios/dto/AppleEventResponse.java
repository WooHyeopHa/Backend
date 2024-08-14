package com.whh.findmuseapi.ios.dto;

import com.whh.findmuseapi.user.entity.PrincipalUser;

public record AppleEventResponse(
    String message,
    PrincipalUser principalUser
) {
}
