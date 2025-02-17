package com.evenly.took.global.exception.dto;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ServerErrorResponse extends ErrorResponse {

	private final ErrorDetail error;

	private ServerErrorResponse(ErrorCode errorCode, ErrorDetail error) {
		super(errorCode);
		this.error = error;
	}

	public static ServerErrorResponse of(ErrorCode errorCode, Exception ex) {
		ErrorDetail errorDetail = new ErrorDetail(ex);
		return new ServerErrorResponse(errorCode, errorDetail);
	}

	private record ErrorDetail(String exception,
							   String message,
							   String stackTrace) {

		private ErrorDetail(Exception ex) {
			this(ex.getClass().getSimpleName(), ex.getMessage(), ex.getStackTrace()[0].getClassName());
		}
	}
}
