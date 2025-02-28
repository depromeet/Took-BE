package com.evenly.took.feature.auth.exception;

import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class InvalidRefreshTokenException extends TookException {

	public InvalidRefreshTokenException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
