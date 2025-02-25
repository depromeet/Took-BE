package com.evenly.took.feature.auth.application;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UuidTokenProvider {

	private static final Duration EXPIRATION_OF_REFRESH_TOKEN = Duration.ofDays(7);

	private final RedisService redisService;

	public String generateRefreshToken(User user) {
		String refreshToken = UUID.randomUUID().toString();
		redisService.setValueWithTTL(user.getId().toString(), refreshToken, EXPIRATION_OF_REFRESH_TOKEN);
		return refreshToken;
	}
}
