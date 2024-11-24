package com.whh.findmuseapi.common.exception;

import com.whh.findmuseapi.common.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class CInternalServerException extends FindmuseException{

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CInternalServerException(String message) {
        super(message);
    }

    public CInternalServerException(ResponseCode code) {
        super(code.getMessage());
    }

    public CInternalServerException(ResponseCode code, String message) {
        super(code.getMessage() + message);
    }
}
