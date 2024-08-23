package com.whh.findmuseapi.common.Exception;

/**
 * class: UnAuthorizationException.
 *
 * @author devminseo
 * @version 8/21/24
 */
public class UnAuthorizationException extends RuntimeException{
    private static final String MSG = "에 대해 권한이 없습니다.";

    public UnAuthorizationException(String target) {
        super(target + MSG);
    }
}
