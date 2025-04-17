package com.evenly.took.feature.user.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	INVALID_ALLOW_PUSH_CONTENT(HttpStatus.BAD_REQUEST, "지원하지 않는 알림 허용 컨텐츠입니다."),
	;

	private final HttpStatus status;
	private final String message;
}
