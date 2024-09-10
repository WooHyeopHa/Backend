package com.whh.findmuseapi.common.Exception;

public class CustomParseException extends RuntimeException {

    public CustomParseException(String token) {
        super("Token : " + token +  "을 파싱하는데 실패하였습니다.");
    }
}
