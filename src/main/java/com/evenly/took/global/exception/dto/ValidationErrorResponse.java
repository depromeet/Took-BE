package com.evenly.took.global.exception.dto;

import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ValidationErrorResponse extends ErrorResponse {

	private final List<FieldErrorDetail> errors;

	private ValidationErrorResponse(ErrorCode errorCode, List<FieldErrorDetail> errors) {
		super(errorCode);
		this.errors = errors;
	}

	public static ValidationErrorResponse of(ErrorCode errorCode, MethodArgumentNotValidException ex) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<FieldErrorDetail> fieldErrorDetails = fieldErrors.stream()
			.map(FieldErrorDetail::new)
			.toList();
		return new ValidationErrorResponse(errorCode, fieldErrorDetails);
	}

	private record FieldErrorDetail(String field,
									String message,
									Object value) {

		private FieldErrorDetail(FieldError fieldError) {
			this(fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue());
		}
	}
}
