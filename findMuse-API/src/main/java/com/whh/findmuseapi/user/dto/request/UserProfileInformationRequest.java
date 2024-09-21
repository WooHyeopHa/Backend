package com.whh.findmuseapi.user.dto.request;


public record UserProfileInformationRequest(
        String nickname,
        String birthYear,
        String gender
) {}
