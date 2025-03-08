package com.evenly.took.feature.auth.client.apple.error;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.client.apple.dto.response.AppleErrorResponse;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppleTokenProviderErrorHandler extends AppleErrorHandler {

	// 애플 인증 코드가 유효하지 않을 때의 에러 코드
	private static final String ERROR_INVALID_GRANT = "invalid_grant";

	// 클라이언트 시크릿이 유효하지 않을 때의 에러 코드
	private static final String ERROR_INVALID_CLIENT = "invalid_client";

	@Override
	protected void handle4xxError(ClientHttpResponse response, String responseBody) throws IOException {
		if (isUnauthorized(response)) {
			throw new TookException(AuthErrorCode.APPLE_INVALID_APP_INFO);
		}

		AppleErrorResponse errorResponse = parseErrorResponse(responseBody);

		if (hasErrorCode(errorResponse, ERROR_INVALID_GRANT)) {
			throw new TookException(AuthErrorCode.APPLE_INVALID_AUTH_CODE);
		}

		if (hasErrorCode(errorResponse, ERROR_INVALID_CLIENT)) {
			throw new TookException(AuthErrorCode.APPLE_INVALID_CLIENT_SECRET);
		}
	}
}
