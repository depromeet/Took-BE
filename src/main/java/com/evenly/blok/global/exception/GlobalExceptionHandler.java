package com.evenly.blok.global.exception;

import com.evenly.blok.global.exception.dto.ErrorResponse;
import com.evenly.blok.global.exception.dto.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(BlokException ex) {
        ErrorResponse response = ErrorResponse.of(ex.getErrorCode());
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST;
        ValidationErrorResponse response = ValidationErrorResponse.of(errorCode, ex);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(Exception ex) {
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = ErrorResponse.of(errorCode);
        // TODO: 로그 추가
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }
}
