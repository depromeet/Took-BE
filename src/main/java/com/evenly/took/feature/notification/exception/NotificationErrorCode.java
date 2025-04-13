package com.evenly.took.feature.notification.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

	FCM_INIT_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 서버 연결 과정에서 에러가 발생하였습니다."),
	INVALID_SEND_TIME(HttpStatus.INTERNAL_SERVER_ERROR, "지원하지 않는 알림 전송 시간입니다."),
	;

	private final HttpStatus status;
	private final String message;
}
