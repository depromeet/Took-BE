package com.evenly.took.feature.auth.client.apple.dto.error;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppleUserInfoProviderErrorHandler extends AppleErrorHandler {

	@Override
	protected void handle4xxError(ClientHttpResponse response) throws IOException {
		if (isUnauthorized(response)) {
			throw new TookException(AuthErrorCode.APPLE_INVALID_ACCESS_TOKEN);
		}
	}
}
