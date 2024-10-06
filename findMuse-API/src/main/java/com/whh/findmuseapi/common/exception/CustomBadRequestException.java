package com.whh.findmuseapi.common.exception;

public class CustomBadRequestException extends RuntimeException {
    
    public CustomBadRequestException(Object detail) {
        super(detail.toString());
    }
}

