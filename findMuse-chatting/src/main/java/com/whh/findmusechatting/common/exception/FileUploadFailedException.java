package com.whh.findmusechatting.common.exception;

public class FileUploadFailedException extends RuntimeException {
    public static final FileUploadFailedException EXCEPTION = new FileUploadFailedException();

    public FileUploadFailedException() {
        super("파일 업로드에 실패했습니다.");
    }

    public FileUploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
