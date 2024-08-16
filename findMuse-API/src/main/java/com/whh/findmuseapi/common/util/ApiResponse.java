package com.whh.findmuseapi.common.util;

import com.whh.findmuseapi.common.constant.ResponseCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    
    /**
     * status : 정상(success), 예외(error), 오류(fail) 중 한 값을 가짐
     * data : 정상(success)의 경우 실제 전송될 데이터를, 오류(fail)의 경우 유효성 검증에 실패한 데이터의 목록을 응답
     * message : 예외(error)의 경우 예외 메시지를 응답
     */
    private int status;
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
    public static ApiResponse<?> createError(ResponseCode responseCode) {
        return new ApiResponse<>(responseCode, null);
    }
    
    // 데이터 유효성 문제
    // Hibernate Validator에 의해 유효하지 않은 데이터로 인해 API 호출이 거부될때 반환
    public static ApiResponse<?> createFail(int status, BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError error : allErrors) {
            if (error instanceof FieldError) {
                errors.put(((FieldError) error).getField(), error.getDefaultMessage());
            } else {
                errors.put( error.getObjectName(), error.getDefaultMessage());
            }
        }
        return new ApiResponse<>(status, errors, null);
    }
    
    private ApiResponse(int status, T data, String message) {
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
