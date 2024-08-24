package com.whh.findmuseapi.common.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    /*
     * 200 OK: 요청 성공
     */
    SUCCESS(HttpStatus.OK, "Request successful."),
    
    /*
     * 201 CREATED: 리소스 생성 성공
     */
    RESOURCE_CREATED(HttpStatus.CREATED, "Resource created successfully."),
    /*
     * 500 INTERNAL_SERVER_ERROR: 형식 분석 오류
     */
    PARSE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Parse error."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: 지원되지 않는 알고리즘
     */
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "Unsupported algorithm."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: 잘못된 키 사양
     */
    INVALID_KEY_SPEC(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid key specification."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: JOSE 처리 오류
     */
    JOSE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "JOSE processing error."),
    
    /*
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request."),
    
    /*
     * 400 BAD_REQUEST: 유효성 검사 오류
     */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: JSON 처리 오류
     */
    JSON_PROCESSING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "JSON processing error."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: JSON 매핑 오류
     */
    JSON_MAPPING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "JSON mapping error."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: 입출력 오류
     */
    IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "I/O error."),
    
    /*
     * 500 INTERNAL_SERVER_ERROR: PEM 처리 오류
     */
    PEM_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "PEM processing error."),
    /*
     * 500 INTERNAL_SERVER_ERROR: 서버 내부 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.");
    
    
    private final HttpStatus status;
    private final String message;
    
    ResponseCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
