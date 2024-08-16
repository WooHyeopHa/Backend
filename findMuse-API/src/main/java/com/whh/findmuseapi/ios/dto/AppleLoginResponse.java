package com.whh.findmuseapi.ios.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter @ToString
@Builder
// 사용자가 애플 로그인 성공 시 받는 응답
public class AppleLoginResponse {
    private String state;
    private String code;
    private String idToken;
    private String user;
}
