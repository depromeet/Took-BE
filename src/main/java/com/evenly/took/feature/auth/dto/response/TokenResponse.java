package com.evenly.took.feature.auth.dto.response;

public record TokenResponse(
	String accessToken,
	String refreshToken
) {
}
