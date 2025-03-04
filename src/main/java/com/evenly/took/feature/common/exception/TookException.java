package com.evenly.took.feature.common.exception;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class TookException extends RuntimeException {

	public TookException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	private final ErrorCode errorCode;
}
