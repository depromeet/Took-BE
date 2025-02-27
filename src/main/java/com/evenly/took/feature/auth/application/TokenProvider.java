package com.evenly.took.feature.auth.application;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.dto.TokenDto;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.security.auth.JwtTokenProvider;
import com.evenly.took.global.security.auth.UuidTokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenProvider {

	private final JwtTokenProvider jwtTokenProvider;
	private final UuidTokenProvider uuidTokenProvider;

	public TokenDto provideTokens(User user) {
		String accessToken = jwtTokenProvider.generateAccessToken(user.getId().toString());
		String refreshToken = uuidTokenProvider.generateRefreshToken(user.getId().toString());
		return new TokenDto(accessToken, refreshToken);
	}

	public String provideAccessTokenByRefreshToken(String refreshToken) {
		String userId = uuidTokenProvider.getUserId(refreshToken);
		return jwtTokenProvider.generateAccessToken(userId);
	}
}
