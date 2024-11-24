package com.whh.findmuseapi.common.exception;

import com.whh.findmuseapi.common.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class CBadRequestException extends FindmuseException {


    @Override
    public HttpStatus getStatus() {
        return ResponseCode.BAD_REQUEST.getStatus();
    }

    public CBadRequestException(String message) {
        super(message);
    }
}

