package com.whh.findmuseapi.ios.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AppleToken {
    
    @Builder
    public static class Request {
        private String code;
        private String client_id;
        private String client_secret;
        private String grant_type;
        private String refresh_token;
    }
    
    public static class Response {
        private String access_token;
        private String expires_in;
        private String id_token;
        private String refresh_token;
        private String token_type;
        private String error;
        
        public String getAccessToken() {
            return access_token;
        }
        
        public String getIdToken() {
            return id_token;
        }
    }
}
