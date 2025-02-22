package com.evenly.took.global.exception.auth.oauth;

import com.evenly.took.global.exception.ErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.Getter;

@Getter
public class OAuthTypeNotFoundException extends TookException {

	public OAuthTypeNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
