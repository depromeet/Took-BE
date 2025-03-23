package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.service.ServiceTest;

class AuthServiceTest extends ServiceTest {

	@MockitoBean
	UserClientComposite userClientComposite;

	@Autowired
	AuthService authService;

	@Test
	void 로그인시_토큰과_사용자_정보를_반환한다() {
		// given
		User user = userFixture.create();
		given(userClientComposite.fetch(any(OAuthType.class), anyString()))
			.willReturn(user);

		// when
		AuthResponse response = authService.loginAndGenerateToken(OAuthType.KAKAO, "code");

		// then
		assertThat(response.token().accessToken()).containsAnyOf(".");
		assertThat(response.token().refreshToken()).isNotBlank();
		assertThat(response.user().name()).isEqualTo(user.getName());
	}
}
