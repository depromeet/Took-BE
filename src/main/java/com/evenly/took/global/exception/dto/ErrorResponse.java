package com.evenly.took.global.exception.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {

	private HttpStatus status;
	private String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	protected ErrorResponse(ErrorCode errorCode) {
		this.status = errorCode.getStatus();
		this.message = errorCode.getMessage();
		this.timestamp = LocalDateTime.now();
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode);
	}
}
