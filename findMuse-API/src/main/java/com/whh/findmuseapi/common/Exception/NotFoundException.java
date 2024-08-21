package com.whh.findmuseapi.common.Exception;

/**
 * class: NotFoundException.
 *
 * @author devminseo
 * @version 8/20/24
 */
public class NotFoundException extends RuntimeException{
    private static final String MSG = "를(을) 찾을 수 없습니다.";

    public NotFoundException(String target) {
        super(target + MSG);
    }
}
