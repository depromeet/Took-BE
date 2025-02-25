package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;
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
		OAuthIdentifier oAuthIdentifier = OAuthIdentifier.builder()
			.oauthId("oauth-id")
			.oauthType(OAuthType.KAKAO)
			.build();
		User user = new User(1L, oAuthIdentifier, "took");

		// when
		String refreshToken = uuidTokenProvider.generateRefreshToken(user);

		// then
		assertThat(refreshToken).isNotNull();
		assertThat(redisService.getValue(user.getId().toString())).isEqualTo(refreshToken);
	}
}
