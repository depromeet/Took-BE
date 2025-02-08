package com.evenly.blok.global.exception.dto;

import com.evenly.blok.global.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private final HttpStatus status;
    private final String message;
    private final LocalDateTime timestamp;

    protected ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }
}
