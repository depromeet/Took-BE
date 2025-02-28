package com.evenly.took.global.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.evenly.took.feature.common.exception.CommonErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.global.exception.dto.ErrorResponse;
import com.evenly.took.global.exception.dto.ServerErrorResponse;
import com.evenly.took.global.exception.dto.ValidationErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ErrorResponse handle(TookException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		return ErrorResponse.of(errorCode);
	}

	@ExceptionHandler
	public ErrorResponse handle(NoResourceFoundException ex) {
		ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_URL;
		return ErrorResponse.of(errorCode);
	}

	@ExceptionHandler
	public ErrorResponse handle(MethodArgumentNotValidException ex) {
		ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_VALUE;
		return ValidationErrorResponse.of(errorCode, ex);
	}

	@ExceptionHandler
	public ErrorResponse handle(Exception ex) {
		// TODO: 로그 추가
		ex.printStackTrace();
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return ServerErrorResponse.of(errorCode, ex);
	}
}
