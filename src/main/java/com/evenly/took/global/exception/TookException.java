package com.evenly.took.global.exception;

import lombok.Getter;

@Getter
public class TookException extends RuntimeException {

	public TookException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	private final ErrorCode errorCode;
}
