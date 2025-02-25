package com.evenly.took.feature.auth.application;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.evenly.took.global.exception.auth.jwt.AuthErrorCode;
import com.evenly.took.global.exception.auth.oauth.InvalidRefreshTokenException;
import com.evenly.took.global.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UuidTokenProvider {

	private static final Duration EXPIRATION_OF_REFRESH_TOKEN = Duration.ofDays(7);

	private final RedisService redisService;

	public String generateRefreshToken(String userId) {
		String refreshToken = UUID.randomUUID().toString();
		redisService.setValueWithTTL(userId, refreshToken, EXPIRATION_OF_REFRESH_TOKEN);
		return refreshToken;
	}

	public void validateToken(String userId, String token) {
		if (!redisService.existsKey(userId)) {
			throw new InvalidRefreshTokenException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
		}
		if (!redisService.getValue(userId).toString().equals(token)) {
			throw new InvalidRefreshTokenException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}
	}
}
