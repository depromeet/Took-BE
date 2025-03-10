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
	;

	private final HttpStatus status;
	private final String message;
}
