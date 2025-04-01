package com.evenly.took.global.aws.sqs.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SQSErrorCode implements ErrorCode {

	MESSAGE_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송에 실패했습니다."),
	MESSAGE_RECEIVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 수신에 실패했습니다."),
	MESSAGE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 삭제에 실패했습니다."),
	QUEUE_URL_FETCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "큐 URL 조회에 실패했습니다.");

	private final HttpStatus status;
	private final String message;
}
