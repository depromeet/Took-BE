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
	INVALID_HTTP_METHOD(HttpStatus.BAD_REQUEST, "지원하지 않는 HTTP 메서드입니다."),
	NO_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "쿼리 파라미터가 필요합니다."),
	NO_REQUEST_MULTIPART_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),
	CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "요청 데이터가 유효성 검증에 실패했습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
