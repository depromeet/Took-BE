package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

	@MockitoBean
	WithdrawService withdrawService;

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

	@Test
	void 로그아웃시_리프레시토큰이_무효화된다() {
		// given
		String refreshToken = "test-refresh-token";

		// when, then
		authService.logout(refreshToken);
	}

	@Test
	void 회원탈퇴시_WithdrawService가_호출된다() {
		// given
		Long userId = 1L;
		String refreshToken = "test-refresh-token";

		doNothing().when(withdrawService).withdraw(anyLong(), anyString());

		// when
		authService.withdraw(userId, refreshToken);

		// then
		verify(withdrawService).withdraw(userId, refreshToken);
	}
}
