package com.whh.findmuseapi.common.exception;

import com.whh.findmuseapi.common.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class CTokenException extends FindmuseException {

    @Override
    public HttpStatus getStatus() {
        return ResponseCode.PARSE_EXCEPTION.getStatus();
    }

    public CTokenException(String message) {
//        super("Token : " + token +  "을 파싱하는데 실패하였습니다.");
        super(message);
    }
    public CTokenException(ResponseCode code, String message) {
        super(code.getMessage() + message);
    }

    public CTokenException(ResponseCode code) {
        super(code.getMessage());
    }
}
