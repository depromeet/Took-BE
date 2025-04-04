package com.evenly.took.feature.card.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardErrorCode implements ErrorCode {

	CARD_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "명함은 최대 3개까지만 생성할 수 있습니다."),
	CANNOT_CRAWL(HttpStatus.BAD_REQUEST, "크롤링에 실패하였습니다."),
	CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "카드를 찾을 수 없습니다."),
	CANNOT_RECEIVE_OWN_CARD(HttpStatus.BAD_REQUEST, "자신의 명함은 수신할 수 없습니다."),
	ALREADY_RECEIVED_CARD(HttpStatus.BAD_REQUEST, "이미 수신한 명함입니다."),
	RECEIVED_CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "수신한 명함을 찾을 수 없습니다."),
	RECEIVED_CARD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 수신 명함에 대한 권한이 없습니다."),
	RECEIVED_CARD_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 수신 명함입니다."),
	INVALID_CRAWL_URL(HttpStatus.BAD_REQUEST, "유효하지 않은 크롤링 링크입니다."),
	;

	private final HttpStatus status;
	private final String message;
}
