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
	INVALID_GOOGLE_TOKEN_REQUEST(HttpStatus.BAD_REQUEST, "Google 승인코드 요청 중 잘못된 요청으로 오류가 발생했습니다."),
	INVALID_GOOGLE_USER_REQUEST(HttpStatus.BAD_REQUEST,
		"Google OAuth V2 정보 요청 중 잘못된 요청으로 오류가 발생했습니다."),
	INVALID_GOOGLE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
		"Google OAuth V2 정보 요청 중 만료된 토큰 또는 승인코드로 오류가 발생했습니다."),
	INVALID_GOOGLE_CONNECTION(HttpStatus.INTERNAL_SERVER_ERROR,
		"Google OAuth 통신 오류: 구글 로그인 서버와의 연결 과정 중 문제가 발생했습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
