package com.evenly.took.global.exception.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;

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
