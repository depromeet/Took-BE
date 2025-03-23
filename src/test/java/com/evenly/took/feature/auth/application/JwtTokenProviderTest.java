package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.evenly.took.feature.auth.config.properties.TokenProperties;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.MockTest;

class JwtTokenProviderTest extends MockTest {

	private User testUser;

	private JwtTokenProvider jwtTokenProvider;

	@BeforeEach
	void setUp() {
		testUser = userFactory.create();

		String accessTokenSecret = "secretKey123secretKey123secretKey123";

		TokenProperties tokenProperties = new TokenProperties(accessTokenSecret, 3600L, 1L);
		jwtTokenProvider = new JwtTokenProvider(tokenProperties);
	}

	@Test
	void accessToken_생성_성공() {
		// given, when
		String token = jwtTokenProvider.generateAccessToken(testUser.getId().toString());

		// then
		assertThat(token).isNotNull();
	}

	@Test
	void accessToken_validate_성공() {
		// given
		String token = jwtTokenProvider.generateAccessToken(testUser.getId().toString());

		// when
		String userId = jwtTokenProvider.getUserId(token);

		// then
		assertThat(token).isNotNull();
		assertThatNoException()
			.isThrownBy(() -> jwtTokenProvider.validateToken(token));
		assertThat(userId).isEqualTo("1");
	}

	@Test
	void validateToken_유효하지_않은_토큰() {
		// given
		String invalidToken = "invalidToken";

		// when & then (예외 발생 검증)
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(invalidToken))
			.isInstanceOf(TookException.class)
			.hasMessage(AuthErrorCode.INVALID_ACCESS_TOKEN.getMessage());
	}
}
