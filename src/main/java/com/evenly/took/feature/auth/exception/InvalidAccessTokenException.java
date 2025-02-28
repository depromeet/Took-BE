package com.evenly.took.feature.auth.exception;

import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class InvalidAccessTokenException extends TookException {

	public InvalidAccessTokenException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
