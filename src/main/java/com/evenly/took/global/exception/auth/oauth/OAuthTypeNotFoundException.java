package com.evenly.took.global.exception.auth.oauth;

import com.evenly.took.global.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthTypeNotFoundException extends RuntimeException {

	private final ErrorCode errorCode;
}
