package com.evenly.took.global.exception.auth.oauth;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthErrorCode implements ErrorCode {

	OAUTH_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "OAuth 타입을 찾을 수 없습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
