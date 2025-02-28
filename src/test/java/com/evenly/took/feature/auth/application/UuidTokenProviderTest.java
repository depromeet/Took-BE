package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.global.redis.RedisService;
import com.evenly.took.global.service.ServiceTest;

class UuidTokenProviderTest extends ServiceTest {

	@Autowired
	UuidTokenProvider uuidTokenProvider;

	@Autowired
	RedisService redisService;

	@Test
	void 리프레쉬_토큰을_생성한다() {
		// given
		String userId = "1";

		// when
		String refreshToken = uuidTokenProvider.generateRefreshToken(userId);

		// then
		assertThat(refreshToken).isNotNull();
		assertThat(redisService.getValue(refreshToken)).isEqualTo(userId);
	}

	@Test
	void 리프레쉬_토큰_생성_후_만료시간에_의한_만료_여부를_확인한다() throws InterruptedException {
		// given
		String userId = "1";

		// when
		String refreshToken = uuidTokenProvider.generateRefreshToken(userId);

		// then
		assertThat(redisService.getValue(refreshToken)).isNotNull();
		Thread.sleep(6000);
		assertThat(redisService.getValue(refreshToken)).isNull();
	}

	@Test
	void 리프레쉬_토큰이_존재하지_않는_경우_예외를_반환한다() {
		assertThatThrownBy(() -> uuidTokenProvider.getUserId("dummy"))
			.isInstanceOf(TookException.class);
	}

	@Test
	void 리프레쉬_토큰이_유효하지_않은_경우_예외를_반환한다() {
		// given
		String userId = "1";
		String refreshToken = uuidTokenProvider.generateRefreshToken(userId);

		// when & then
		assertThatThrownBy(() -> uuidTokenProvider.getUserId(refreshToken + "invalid"))
			.isInstanceOf(TookException.class);
	}
}
