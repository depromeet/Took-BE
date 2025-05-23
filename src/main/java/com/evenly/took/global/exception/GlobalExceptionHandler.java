package com.evenly.took.global.exception;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.evenly.took.feature.common.exception.CommonErrorCode;
import com.evenly.took.global.exception.dto.ErrorResponse;
import com.evenly.took.global.exception.dto.RequestParameterErrorResponse;
import com.evenly.took.global.exception.dto.ServerErrorResponse;
import com.evenly.took.global.exception.dto.ValidationErrorResponse;

import jakarta.validation.ConstraintViolationException;

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
	public ErrorResponse handle(HttpRequestMethodNotSupportedException ex) {
		ErrorCode errorCode = CommonErrorCode.INVALID_HTTP_METHOD;
		return ErrorResponse.of(errorCode);
	}

	@ExceptionHandler
	public ErrorResponse handle(MethodArgumentTypeMismatchException ex) {
		ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_PARAMETER;
		return RequestParameterErrorResponse.of(errorCode, ex);
	}

	@ExceptionHandler
	public ErrorResponse handle(MissingServletRequestParameterException ex) {
		ErrorCode errorCode = CommonErrorCode.NO_REQUEST_PARAMETER;
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

	@ExceptionHandler
	public ErrorResponse handle(MissingServletRequestPartException ex) {
		ErrorCode errorCode = CommonErrorCode.NO_REQUEST_MULTIPART_PARAMETER;
		return ServerErrorResponse.of(errorCode, ex);
	}

	@ExceptionHandler
	public ErrorResponse handle(ConstraintViolationException ex) {
		ErrorCode errorCode = CommonErrorCode.CONSTRAINT_VIOLATION;
		return ServerErrorResponse.of(errorCode, ex);
	}
}
