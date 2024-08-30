package com.whh.findmuseapi.common.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    /*
     * 200 OK: 요청 성공
     */
    SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    
    /*
     * 201 CREATED: 리소스 생성 성공
     */
    RESOURCE_CREATED(HttpStatus.CREATED, "새로운 리소스가 성공적으로 생성되었습니다."),
    /*
     * 500 INTERNAL_SERVER_ERROR: 형식 분석 오류
     */
    PARSE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 파싱 중 에러가 발생했습니다."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: 지원되지 않는 알고리즘
     */
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "지원되지 않는 암호화 알고리즘이 사용되었습니다."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: 잘못된 키 사양
     */
    INVALID_KEY_SPEC(HttpStatus.INTERNAL_SERVER_ERROR, "RSA 개인키(PEM)를 확인해주세요."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: JOSE 처리 오류
     */
    JOSE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "검증되지 않은 토큰입니다."),
    
    /*
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    
    /*
     * 400 BAD_REQUEST: 유효성 검사 오류
     */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "검증되지 않은 값입니다."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: JSON 처리 오류
     */
    JSON_PROCESSING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "JSON으로 변환할 수 없는 형식입니다."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: 입출력 오류
     */
    IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "입출력 과정에서 오류가 발생했습니다."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: 서버 내부 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "요청 처리 중 서버에서 오류가 발생했습니다.");
    
    
    private final HttpStatus status;
    private final String message;
    
    ResponseCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
