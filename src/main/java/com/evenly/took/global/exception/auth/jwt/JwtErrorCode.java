package com.evenly.took.global.exception.auth.jwt;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {

	JWT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "JWT를 찾을 수 없습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
