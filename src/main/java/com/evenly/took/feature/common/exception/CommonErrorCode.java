package com.evenly.took.feature.common.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 관리자에게 문의하세요."),
	INVALID_REQUEST_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 요청값입니다."),
	INVALID_REQUEST_URL(HttpStatus.BAD_REQUEST, "유효하지 않은 경로입니다."),
	INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "유효하지 않은 쿼리 파라미터입니다."),
	;

	private final HttpStatus status;
	private final String message;
}
