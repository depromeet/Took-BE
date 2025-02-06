package com.evenly.blok.global.exception.dto;

import com.evenly.blok.global.exception.ErrorCode;
import java.util.List;
import lombok.Getter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    public record FieldErrorDetail(String field,
                                   String message,
                                   Object value) {

        public FieldErrorDetail(FieldError fieldError) {
            this(fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue());
        }
    }
}
