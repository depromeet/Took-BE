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
        ErrorResponse response = new ErrorResponse("서버 관리자에게 문의하세요.");
        return ResponseEntity
                .internalServerError()
                .body(response);
    }
}
