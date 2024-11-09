package com.whh.findmusechatting.common.exception;

public class FileDeleteFailedException extends RuntimeException {
    public static final FileDeleteFailedException EXCEPTION = new FileDeleteFailedException();

    public FileDeleteFailedException() {
        super("파일 삭제에 실패했습니다.");
    }

    public FileDeleteFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
