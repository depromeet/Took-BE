package com.evenly.took.feature.auth.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	OAUTH_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "OAuth 타입을 찾을 수 없습니다."),
	JWT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "JWT를 찾을 수 없습니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "refresh token이 만료되었습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "access token이 유효하지 않습니다."),

	// Apple 관련 에러 코드
	APPLE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "애플 서버에 오류가 발생했습니다."),
	APPLE_INVALID_APP_INFO(HttpStatus.UNAUTHORIZED, "애플 앱 정보가 올바르지 않습니다."),
	APPLE_INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 애플 인증 코드입니다."),
	APPLE_INVALID_CLIENT_SECRET(HttpStatus.UNAUTHORIZED, "애플 클라이언트 시크릿이 유효하지 않습니다."),
	APPLE_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "애플 엑세스 토큰이 유효하지 않습니다."),
	APPLE_INVALID_ID_TOKEN(HttpStatus.BAD_REQUEST, "애플 ID 토큰이 유효하지 않습니다.");

	private final HttpStatus status;
	private final String message;
}
