package com.whh.findmuseapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FindMuseApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FindMuseApiApplication.class, args);
    }
    
}
