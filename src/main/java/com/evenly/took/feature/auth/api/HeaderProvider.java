package com.evenly.took.feature.auth.api;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.dto.response.AuthResponse;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class HeaderProvider {

	private static final String HEADER_KEY_OF_AUTH = "Authorization";
	private static final String HEADER_VALUE_PREFIX_OF_AUTH = "Bearer %s %s";

	public void setAuthHeader(HttpServletResponse response, AuthResponse authResponse) {
		String value = buildAuthHeaderValue(authResponse.accessToken(), authResponse.refreshToken());
		response.setHeader(HEADER_KEY_OF_AUTH, value);
	}

	private String buildAuthHeaderValue(String accessToken, String refreshToken) {
		return String.format(HEADER_VALUE_PREFIX_OF_AUTH, accessToken, refreshToken);
	}
}
