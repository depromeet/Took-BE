package com.evenly.took.feature.notification.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

	FCM_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 서버 연결 과정에서 에러가 발생하였습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
