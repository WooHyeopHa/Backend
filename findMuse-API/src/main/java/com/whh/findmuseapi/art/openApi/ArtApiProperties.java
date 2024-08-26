package com.whh.findmuseapi.art.openApi;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "art")
public class ArtApiProperties {
    private String serviceKey;
    private String musical;         // 뮤지컬
    private String drama;           // 연극
    private String concert;         // 콘서트
    private String concertMagic;    // 마술
    private String dance;           // 무용
    private String danceBasic;      // 대중무용
    private String classic;         // 클래식
}
