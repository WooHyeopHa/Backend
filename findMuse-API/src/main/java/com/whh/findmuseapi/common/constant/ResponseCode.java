package com.whh.findmuseapi.common.constant;

import lombok.Getter;

@Getter
public enum ResponseCode {
    
    SUCCESS(200, "요청이 성공적으로 처리되었습니다"),
    CREATED(201, "자원이 성공적으로 생성되었습니다"),
    ACCEPTED(202, "요청이 접수되었지만 처리 중입니다"),
    NO_CONTENT(204, "요청에 대해 반환할 내용이 없습니다"),
    
    BAD_REQUEST(400, "잘못된 요청입니다"),
    UNAUTHORIZED(401, "인증이 필요합니다"),
    FORBIDDEN(403, "접근이 금지되었습니다"),
    NOT_FOUND(404, "자원을 찾을 수 없습니다"),
    
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다"),
    NOT_IMPLEMENTED(501, "구현되지 않은 기능입니다"),
    BAD_GATEWAY(502, "잘못된 게이트웨이입니다"),
    SERVICE_UNAVAILABLE(503, "서비스를 사용할 수 없습니다"),
    GATEWAY_TIMEOUT(504, "게이트웨이 시간 초과입니다");
    
    private final int status;
    private final String message;
    
    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
