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
	INVALID_GOOGLE_TOKEN(HttpStatus.UNAUTHORIZED, "Google OAuth 토큰 발급 실패: 유효한 토큰 응답을 받지 못했습니다."),
	INVALID_GOOGLE_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED,
		"Google OAuth 사용자 정보 조회 실패: 응답 상태가 비정상적이거나 사용자 정보를 확인할 수 없습니다."),
	INVALID_GOOGLE_CONNECTION(HttpStatus.BAD_GATEWAY,
		"Google OAuth 통신 오류: 사용자 정보를 가져오는 중 HTTP 클라이언트 에러가 발생했습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
