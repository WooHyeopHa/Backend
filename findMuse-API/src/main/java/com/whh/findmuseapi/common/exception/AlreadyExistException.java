package com.whh.findmuseapi.common.exception;

/**
 * class: NotFoundException.
 *
 * @author devminseo
 * @version 8/20/24
 */
public class AlreadyExistException extends RuntimeException{
    private static final String MSG = "은(는) 이미 존재합니다.";

    public AlreadyExistException(String target) {
        super(target + MSG);
    }
}
