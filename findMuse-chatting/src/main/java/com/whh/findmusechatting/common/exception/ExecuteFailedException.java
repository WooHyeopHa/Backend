package com.whh.findmusechatting.common.exception;

public class ExecuteFailedException extends RuntimeException {
    public static final ExecuteFailedException EXCEPTION = new ExecuteFailedException();

    public ExecuteFailedException() {
        super("작업 실행에 실패했습니다.");
    }

    public ExecuteFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
