package com.evenly.took.feature.auth.application;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.evenly.took.global.config.properties.jwt.AuthProperties;
import com.evenly.took.global.exception.auth.jwt.AuthErrorCode;
import com.evenly.took.global.exception.auth.oauth.InvalidRefreshTokenException;
import com.evenly.took.global.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UuidTokenProvider {

	private final AuthProperties authProperties;
	private final RedisService redisService;

	public String generateRefreshToken(String userId) {
		String refreshToken = UUID.randomUUID().toString();
		Duration expiration = Duration.ofSeconds(authProperties.refreshTokenExpirationTime());
		redisService.setValueWithTTL(userId, refreshToken, expiration);
		return refreshToken;
	}

	public void validateToken(String userId, String token) {
		Object savedToken = redisService.getValue(userId);
		if (savedToken == null) {
			throw new InvalidRefreshTokenException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
		}
		if (!savedToken.toString().equals(token)) {
			throw new InvalidRefreshTokenException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}
	}
}
