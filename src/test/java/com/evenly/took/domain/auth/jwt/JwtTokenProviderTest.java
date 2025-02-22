package com.evenly.took.domain.auth.jwt;

import static com.evenly.took.global.domain.TestUserFactory.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.evenly.took.domain.user.domain.User;
import com.evenly.took.global.config.properties.jwt.JwtProperties;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.MockTest;

class JwtTokenProviderTest extends MockTest {

	private User testUser;

	private JwtTokenProvider jwtTokenProvider;

	@BeforeEach
	void setUp() {
		testUser = createMockGoogleUser();

		String accessTokenSecret = "secretKey123secretKey123secretKey123";

		JwtProperties jwtProperties = new JwtProperties(accessTokenSecret, 3600L);
		jwtTokenProvider = new JwtTokenProvider(jwtProperties);
	}

	@Test
	void accessToken_생성_성공() {
		// given, when
		String token = jwtTokenProvider.generateAccessToken(testUser);

		// then
		assertThat(token).isNotNull();
	}

	@Test
	void accessToken_validate_성공() {
		// given
		String token = jwtTokenProvider.generateAccessToken(testUser);

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
			.isInstanceOf(TookException.class);
	}
}
