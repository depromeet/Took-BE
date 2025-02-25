package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.domain.TestUserFactory;
import com.evenly.took.global.security.auth.JwtTokenProvider;
import com.evenly.took.global.security.auth.UuidTokenProvider;
import com.evenly.took.global.security.client.AuthCodeRequestUrlProviderComposite;
import com.evenly.took.global.security.client.UserClientComposite;
import com.evenly.took.global.service.ServiceTest;

class OAuthServiceTest extends ServiceTest {

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
	void 로그인시_토큰과_사용자_정보를_반환한다() {
		// given
		User user = TestUserFactory.createMockUser("took");
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
