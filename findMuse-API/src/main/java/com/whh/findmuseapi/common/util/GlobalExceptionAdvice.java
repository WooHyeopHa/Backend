package com.whh.findmuseapi.common.util;

import com.whh.findmuseapi.common.exception.*;
import com.whh.findmuseapi.common.constant.ResponseCode;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        List<String> result = e.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        return ApiResponse.createError(ResponseCode.VALIDATION_ERROR, result.toString());
    }
    
    // 500 INTERNAL_SERVER_ERROR: 형식 분석 오류 처리
    @ExceptionHandler(CTokenException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleParseException(CTokenException ex) {
        ex.printStackTrace();
        return ApiResponse.createError(ResponseCode.PARSE_EXCEPTION, ex.getMessage());
    }
    
    // 400 BAD_REQUEST: 잘못된 요청
    @ExceptionHandler(CBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBadRequestException(CBadRequestException ex) {
        ex.printStackTrace();
        return ApiResponse.createError(ex.getStatus(), ex.getMessage());
    }

    // 401 UNAUTHORIZED: 권한 없음
    @ExceptionHandler(CUnAuthorizationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleAuthorizationException(CUnAuthorizationException ex) {
        ex.printStackTrace();
        return ApiResponse.createError(ex.getStatus(), ex.getMessage());
    }

    // 403 FORBIDDEN: 권한 없음
    @ExceptionHandler(CForbiddenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleAuthorizationException(CForbiddenException ex) {
        ex.printStackTrace();
        return ApiResponse.createError(ex.getStatus(), ex.getMessage());
    }
    
    // 500 INTERNAL_SERVER_ERROR: 지원되지 않는 알고리즘 처리
//    @ExceptionHandler(NoSuchAlgorithmException.class)
//    public ApiResponse<?> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex) {
//        ex.printStackTrace();
//        return ApiResponse.createError(ResponseCode.NO_SUCH_ALGORITHM, ex.getMessage());
//    }
    
    // 500 INTERNAL_SERVER_ERROR: 잘못된 키 사양 처리
//    @ExceptionHandler(InvalidKeySpecException.class)
//    public ApiResponse<?> handleInvalidKeySpecException(InvalidKeySpecException ex) {
//        ex.printStackTrace();
//        return ApiResponse.createError(ResponseCode.INVALID_KEY_SPEC, ex.getMessage());
//    }
    
    // 500 INTERNAL_SERVER_ERROR: JOSE 처리 오류 처리
//    @ExceptionHandler(com.nimbusds.jose.JOSEException.class)
//    public ApiResponse<?> handleJOSEException(com.nimbusds.jose.JOSEException ex) {
//        ex.printStackTrace();
//        return ApiResponse.createError(ResponseCode.JOSE_EXCEPTION, ex.getMessage());
//    }
//
    // 500 INTERNAL_SERVER_ERROR: JSON 처리 오류 처리
    @ExceptionHandler(CInternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleServerException(CInternalServerException ex) {
        ex.printStackTrace();
        return ApiResponse.createError(ex.getStatus(), ex.getMessage());
    }

//    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
//    public ApiResponse<?> handleJsonProcessingException(com.fasterxml.jackson.core.JsonProcessingException ex) {
//        ex.printStackTrace();
//        return ApiResponse.createError(ResponseCode.JSON_PROCESSING_EXCEPTION, ex.getMessage());
//    }
    
    // 500 INTERNAL_SERVER_ERROR: 입출력 오류 처리
//    @ExceptionHandler(IOException.class)
//    public ApiResponse<?> handleIOException(IOException ex) {
//        ex.printStackTrace();
//        return ApiResponse.createError(ResponseCode.IO_EXCEPTION, ex.getMessage());
//    }
    
    // 그 외의 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleAllExceptions(Exception ex) {
        ex.printStackTrace();
        return ApiResponse.createError(ResponseCode.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
