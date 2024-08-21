package com.whh.findmuseapi.common.util;

import java.util.List;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * class: GlobalExceptionAdvice.
 * API 서버 전역에서 발생하는 예외를 공통으로 처리하는 클래스입니다.
 * @author devminseo
 * @version 8/21/24
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 컨트롤러들의 request DTO에 대한 공통 Validation 핸들러 처리 메서드 입니다.
     *
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        List<String> result = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        return null;
    }
}
