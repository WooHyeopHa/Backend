package com.whh.findmusechatting.common.exception;

public class FileDownloadFailedException extends RuntimeException {
    public static final FileDownloadFailedException EXCEPTION = new FileDownloadFailedException();

    public FileDownloadFailedException() {
        super("파일 다운로드에 실패했습니다.");
    }

    public FileDownloadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}