package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.config.testcontainers.RedisTestConfig;
import com.evenly.took.global.security.client.AuthCodeRequestUrlProviderComposite;
import com.evenly.took.global.security.client.UserClientComposite;
import com.evenly.took.global.security.jwt.JwtTokenProvider;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class OAuthServiceTest { // TODO 상위 클래스 추출

	OAuthService oauthService;

	@Mock
	UserClientComposite userClientComposite;

	@Mock
	AuthCodeRequestUrlProviderComposite authCodeComposite;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UuidTokenProvider uuidTokenProvider;

	@BeforeEach
	void setUp() {
		oauthService = new OAuthService(
			authCodeComposite,
			userClientComposite,
			userRepository,
			jwtTokenProvider,
			uuidTokenProvider);
	}

	@Test
	void name() {
		// given
		OAuthIdentifier oauthIdentifier = OAuthIdentifier.builder()
			.oauthId("oauth-id")
			.oauthType(OAuthType.KAKAO)
			.build();
		User user = User.builder()
			.oauthIdentifier(oauthIdentifier)
			.name("took")
			.build();
		given(userClientComposite.fetch(any(OAuthType.class), anyString()))
			.willReturn(user);

		// when
		AuthResponse response = oauthService.loginAndGenerateToken(OAuthType.KAKAO, "code");

		// then
		assertThat(response.token().accessToken()).containsAnyOf(".");
		assertThat(response.token().refreshToken()).isNotBlank();
		assertThat(response.user().name()).isEqualTo("took");
	}
}
