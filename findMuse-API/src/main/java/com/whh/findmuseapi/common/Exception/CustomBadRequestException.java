package com.whh.findmuseapi.common.Exception;

public class CustomBadRequestException extends RuntimeException {
    
    public CustomBadRequestException(Object detail) {
        super(detail.toString());
    }
}

