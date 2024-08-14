package com.whh.findmuseapi.common.config;

import feign.Retryer;
import feign.Retryer.Default;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRetryConfig {
    @Bean
    public Retryer retryer() {
        return new Default(1000, 1500, 1);
    }
}
