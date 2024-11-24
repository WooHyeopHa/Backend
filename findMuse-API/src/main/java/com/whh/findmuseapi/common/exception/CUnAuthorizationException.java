package com.whh.findmuseapi.common.exception;


import com.whh.findmuseapi.common.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class CUnAuthorizationException extends FindmuseException{

    @Override
    public HttpStatus getStatus() {
        return ResponseCode.UNAUTHORIZED_REQUEST‎.getStatus();
    }

    public CUnAuthorizationException(String message) {
        super(message);
    }

    public CUnAuthorizationException() {
        super(ResponseCode.UNAUTHORIZED_REQUEST‎.getMessage());
    }

    public CUnAuthorizationException(ResponseCode code) {
        super(code.getMessage());
    }
}
