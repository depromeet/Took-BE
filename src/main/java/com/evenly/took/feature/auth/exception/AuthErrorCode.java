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
	KAKAO_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 로그인 서버와의 연결 과정에서 문제가 발생하였습니다."),
	KAKAO_INVALID_APP_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 애플리케이션 정보가 유효하지 않습니다."),
	KAKAO_INVALID_AUTH_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 인증 코드는 오직 한 번만 사용 가능합니다."),
	;

	private final HttpStatus status;
	private final String message;
}
