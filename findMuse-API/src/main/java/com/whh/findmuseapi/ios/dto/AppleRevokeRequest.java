package com.whh.findmuseapi.ios.dto;

import lombok.Builder;

@Builder
public record AppleRevokeRequest(
    String client_id,
    String client_secret,
    String token,
    String token_type_hint
    
) {
}
