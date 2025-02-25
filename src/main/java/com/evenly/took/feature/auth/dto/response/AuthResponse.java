package com.evenly.took.feature.auth.dto.response;

import com.evenly.took.feature.user.domain.User;

public record AuthResponse(
	String accessToken,
	String refreshToken,
	UserResponse user
) {

	public AuthResponse(String accessToken, String refreshToken, User user) {
		this(accessToken, refreshToken, new UserResponse(user));
	}
}
