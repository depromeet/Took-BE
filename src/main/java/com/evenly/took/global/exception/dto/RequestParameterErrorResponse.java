package com.evenly.took.global.exception.dto;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class RequestParameterErrorResponse extends ErrorResponse {

	private final ErrorDetail error;

	private RequestParameterErrorResponse(ErrorCode errorCode, ErrorDetail error) {
		super(errorCode);
		this.error = error;
	}

	public static RequestParameterErrorResponse of(ErrorCode errorCode, MethodArgumentTypeMismatchException ex) {
		return new RequestParameterErrorResponse(errorCode, new ErrorDetail(ex));
	}

	private record ErrorDetail(String field,
							   Object value) {

		private ErrorDetail(MethodArgumentTypeMismatchException ex) {
			this(ex.getName(), ex.getValue());
		}
	}
}
