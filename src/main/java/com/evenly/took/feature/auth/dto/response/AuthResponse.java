package com.evenly.took.feature.auth.dto.response;

import com.evenly.took.feature.auth.dto.TokenDto;
import com.evenly.took.feature.user.domain.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record AuthResponse(
	@Schema(description = "액세스 토큰 및 리프레시 토큰 정보") TokenResponse token,
	@Schema(description = "로그인 사용자 정보") UserResponse user
) {

	public AuthResponse(TokenDto tokens, User user) {
		this(new TokenResponse(tokens.accessToken(), tokens.refreshToken()),
			new UserResponse(user));
	}
}
