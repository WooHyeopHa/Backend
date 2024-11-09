package com.whh.findmusechatting.common.exception;

public class FileCheckFailedException extends RuntimeException {
    public static final FileCheckFailedException EXCEPTION = new FileCheckFailedException();

    public FileCheckFailedException() {
        super("파일 확인에 실패했습니다.");
    }

    public FileCheckFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}