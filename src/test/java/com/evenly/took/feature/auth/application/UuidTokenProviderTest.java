package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.config.testcontainers.RedisTestConfig;
import com.evenly.took.global.redis.RedisService;
import com.evenly.took.global.service.MockTest;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class UuidTokenProviderTest extends MockTest { // TODO Import 상위로 추출

	@Autowired
	UuidTokenProvider uuidTokenProvider;

	@Autowired
	RedisService redisService;

	@Test
	void refreshToken을_생성한다() {
		// given
		String userId = "1";

		// when
		String refreshToken = uuidTokenProvider.generateRefreshToken(userId);

		// then
		assertThat(refreshToken).isNotNull();
		assertThat(redisService.getValue(userId)).isEqualTo(refreshToken);
	}
}
