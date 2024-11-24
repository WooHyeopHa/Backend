package com.whh.findmuseapi.common.util;

import com.whh.findmuseapi.common.constant.ResponseCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    
    /**
     * status : 정상(success), 생성(created), 예외(error), 오류(fail) 중 한 값을 가짐
     * data : 정상(success)의 경우 실제 전송될 데이터를, 오류(fail)의 경우 유효성 검증에 실패한 데이터의 목록을 응답
     * message : 예외(error)의 경우 예외 메시지를 응답
     */
    private HttpStatus status;
    private T data;
    private String message;
    
    // 정상
    public static <T> ApiResponse<T> createSuccess(ResponseCode responseCode, T data) {
        return new ApiResponse<>(responseCode, data);
    }
    
    public static ApiResponse<?> createSuccessWithNoContent(ResponseCode responseCode) {
        return new ApiResponse<>(responseCode, null);
    }
    
    // 예외 발생으로 API 호출 실패시 반환
    public static ApiResponse<?> createError(ResponseCode responseCode, String errorMessage) {
        return new ApiResponse<>(responseCode.getStatus(), null, errorMessage);
    }
    
    public static ApiResponse<?> createError(ResponseCode responseCode) {
        return new ApiResponse<>(responseCode.getStatus(), null, responseCode.getMessage());
    }

    public static ApiResponse<?> createError(HttpStatus status, String message) {
        return new ApiResponse<>(status, "ERROR", message);
    }
    
    private ApiResponse(HttpStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
    
    private ApiResponse(ResponseCode responseCode, T data) {
        this.status = responseCode.getStatus();
        this.data = data;
        this.message = responseCode.getMessage();
    }
}
