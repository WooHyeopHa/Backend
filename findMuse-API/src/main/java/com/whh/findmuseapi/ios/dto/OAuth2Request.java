package com.whh.findmuseapi.ios.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2Request {
    private final String accountId;
    private final String name;
    private final String email;
}
