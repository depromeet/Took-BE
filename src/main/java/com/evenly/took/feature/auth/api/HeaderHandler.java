package com.evenly.took.feature.auth.api;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.global.exception.auth.jwt.AuthErrorCode;
import com.evenly.took.global.exception.auth.oauth.InvalidTokenException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HeaderHandler {

	private static final String HEADER_KEY_OF_AUTH = "Authorization";
	private static final String HEADER_VALUE_FORMAT_OF_AUTH = "Bearer %s %s";
	private static final String HEADER_VALUE_PREFIX_OF_AUTH = "Bearer ";
	private static final String HEADER_VALUE_DELIMITER_OF_AUTH = " ";

	public void setAuthHeader(HttpServletResponse response, TokenResponse tokenResponse) {
		String value = buildAuthHeaderValue(tokenResponse.accessToken(), tokenResponse.refreshToken());
		response.setHeader(HEADER_KEY_OF_AUTH, value);
	}

	private String buildAuthHeaderValue(String accessToken, String refreshToken) {
		return String.format(HEADER_VALUE_FORMAT_OF_AUTH, accessToken, refreshToken);
	}

	public String resolveAccessToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(HEADER_KEY_OF_AUTH);
		validateAuthHeader(bearerToken);
		return bearerToken.split(HEADER_VALUE_DELIMITER_OF_AUTH)[1];
	}

	public String resolveRefreshToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(HEADER_KEY_OF_AUTH);
		validateAuthHeader(bearerToken);
		return bearerToken.split(HEADER_VALUE_DELIMITER_OF_AUTH)[2];
	}

	private void validateAuthHeader(String bearerToken) {
		if (bearerToken == null || !bearerToken.startsWith(HEADER_VALUE_PREFIX_OF_AUTH)) {
			throw new InvalidTokenException(AuthErrorCode.JWT_UNAUTHORIZED);
		}
	}
}
