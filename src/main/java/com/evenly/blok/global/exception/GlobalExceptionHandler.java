package com.evenly.blok.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(BlokException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorCode.getResponse());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(Exception ex) {
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        // TODO: 로그 추가
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorCode.getResponse());
    }
}
