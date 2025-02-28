package com.evenly.took.feature.auth.api;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.auth.exception.InvalidTokenException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class HeaderHandler {

	private static final String HEADER_KEY_OF_AUTH = "Authorization";
	private static final String HEADER_VALUE_PREFIX_OF_AUTH = "Bearer ";

	public String resolveAccessToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(HEADER_KEY_OF_AUTH);
		validateAuthHeader(bearerToken);
		return bearerToken.substring(HEADER_VALUE_PREFIX_OF_AUTH.length());
	}

	private void validateAuthHeader(String bearerToken) {
		if (bearerToken == null || !bearerToken.startsWith(HEADER_VALUE_PREFIX_OF_AUTH)) {
			throw new InvalidTokenException(AuthErrorCode.JWT_UNAUTHORIZED);
		}
	}
}
