package com.evenly.took.global.location;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocationErrorCode implements ErrorCode {

	INVALID_PARSE_LOCATION(HttpStatus.INTERNAL_SERVER_ERROR, "위치를 파싱하는데 실패했습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
