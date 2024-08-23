package com.whh.findmuseapi.ios.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "apple")
public class AppleProperties {
    private String teamId;
    private String loginKey;
    private String clientId;
    private String redirectUrl;
    private String keyPath;
    private String authUrl;
}
