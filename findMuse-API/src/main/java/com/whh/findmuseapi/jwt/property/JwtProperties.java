package com.whh.findmuseapi.jwt.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    
    private TokenProperty access;
    private TokenProperty refresh;
}
