package com.evenly.took.global.exception.auth.jwt;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	JWT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "JWT를 찾을 수 없습니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "refresh token이 만료되었습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "refresh token이 유효하지 않습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
