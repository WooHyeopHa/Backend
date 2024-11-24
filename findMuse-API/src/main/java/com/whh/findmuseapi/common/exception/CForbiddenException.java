package com.whh.findmuseapi.common.exception;

import com.whh.findmuseapi.common.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class CForbiddenException extends FindmuseException{
    @Override
    public HttpStatus getStatus() {
        return ResponseCode.INVALID_REQUEST‎.getStatus();
    }

    public CForbiddenException(String message) {
        super(ResponseCode.INVALID_REQUEST‎.getMessage() + message);
    }

    public CForbiddenException() {
        super(ResponseCode.INVALID_REQUEST‎.getMessage());
    }
}
