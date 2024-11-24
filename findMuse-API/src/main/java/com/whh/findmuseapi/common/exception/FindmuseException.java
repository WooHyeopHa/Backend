package com.whh.findmuseapi.common.exception;

import org.springframework.http.HttpStatus;

public abstract class FindmuseException extends RuntimeException {
    public abstract HttpStatus getStatus();

    public FindmuseException(String message) {
        super(message);
    }
}