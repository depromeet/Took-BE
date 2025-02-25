package com.evenly.took.feature.auth.dto.response;

import com.evenly.took.feature.user.domain.User;

public record AuthResponse(
	TokenResponse token,
	UserResponse user
) {

	public AuthResponse(String accessToken, String refreshToken, User user) {
		this(new TokenResponse(accessToken, refreshToken), new UserResponse(user));
	}
}
