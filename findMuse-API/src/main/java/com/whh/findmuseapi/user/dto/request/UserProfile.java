package com.whh.findmuseapi.user.dto.request;

public record UserProfile() {

    public record NicknameRequest(String nickname) {}

    public record LocationRequest(String location) {}
}
