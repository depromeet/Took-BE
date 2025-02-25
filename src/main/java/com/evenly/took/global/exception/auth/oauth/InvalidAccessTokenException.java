package com.evenly.took.global.exception.auth.oauth;

import com.evenly.took.global.exception.ErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.Getter;

@Getter
public class InvalidAccessTokenException extends TookException {

	public InvalidAccessTokenException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
