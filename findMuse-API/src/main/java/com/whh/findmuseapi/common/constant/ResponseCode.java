package com.whh.findmuseapi.common.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    
    SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    
    RESOURCE_CREATED(HttpStatus.CREATED, "새로운 리소스가 성공적으로 생성되었습니다."),
    
    PARSE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 파싱 중 에러가 발생했습니다."),
    
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "지원되지 않는 암호화 알고리즘이 사용되었습니다."),
    
    INVALID_KEY_SPEC(HttpStatus.INTERNAL_SERVER_ERROR, "키 스펙이 잘못되었습니다. RSA 개인키(PEM)를 확인해주세요."),
    
    JOSE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "JOSE 처리 중 오류가 발생했습니다."),
    
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "검증되지 않은 값입니다."),
    
    JSON_PROCESSING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "JSON으로 변환할 수 없는 형식입니다."),
    
    IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "입출력 과정에서 오류가 발생했습니다."),
    
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰의 유효기간이 만료됐습니다."),
    
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    
    UNAUTHORIZED_REQUEST‎(HttpStatus.UNAUTHORIZED, "유효한 자격증명을 제공하지 않았습니다."),
    
    INVALID_REQUEST‎(HttpStatus.FORBIDDEN, "요청에 대한 접근 권한이 존재하지 않습니다."),
    
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "요청 처리 중 서버에서 오류가 발생했습니다.");
    
    
    private final HttpStatus status;
    private final String message;
    
    ResponseCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
