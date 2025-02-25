package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.config.testcontainers.RedisTestConfig;
import com.evenly.took.global.exception.auth.oauth.InvalidRefreshTokenException;
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
	void 갱신_토큰을_생성한다() {
		// given
		String userId = "1";

		// when
		String refreshToken = uuidTokenProvider.generateRefreshToken(userId);

		// then
		assertThat(refreshToken).isNotNull();
		assertThat(redisService.getValue(userId)).isEqualTo(refreshToken);
	}

	@Test
	void 갱신_토큰_생성_후_만료시간에_의한_만료_여부를_확인한다() throws InterruptedException {
		// given
		String userId = "1";

		// when
		uuidTokenProvider.generateRefreshToken(userId);

		// then
		assertThat(redisService.getValue(userId)).isNotNull();
		Thread.sleep(1500);
		assertThat(redisService.getValue(userId)).isNull();
	}

	@Test
	void 갱신_토큰이_존재하지_않는_경우_예외를_반환한다() {
		// given
		String userId = "invalid";

		// when & then
		assertThatThrownBy(() -> uuidTokenProvider.validateToken(userId, "dummy"))
			.isInstanceOf(InvalidRefreshTokenException.class);
	}

	@Test
	void 갱신_토큰이_유효하지_않은_경우_예외를_반환한다() {
		// given
		String userId = "1";
		String refreshToken = uuidTokenProvider.generateRefreshToken(userId);

		// when & then
		assertThatThrownBy(() -> uuidTokenProvider.validateToken(userId, refreshToken + "invalid"))
			.isInstanceOf(InvalidRefreshTokenException.class);
	}
}
