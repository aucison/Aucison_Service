package com.example.aucison_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detailMessage;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = "";
    }

    public AppException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}