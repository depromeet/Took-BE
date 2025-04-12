package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.request.LoginRequest;
import com.evenly.took.feature.auth.dto.request.WithdrawRequest;
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
		LoginRequest request = new LoginRequest("fcmToken");
		AuthResponse response = authService.loginAndGenerateToken(OAuthType.KAKAO, "code", request);

		// then
		assertThat(response.token().accessToken()).containsAnyOf(".");
		assertThat(response.token().refreshToken()).isNotBlank();
		assertThat(response.user().name()).isEqualTo(user.getName());
		assertThat(response.isFirstLogin()).isTrue();
	}

	@Test
	void 로그아웃시_리프레시토큰이_무효화된다() {
		// given
		String refreshToken = "test-refresh-token";

		// when, then
		authService.logout(refreshToken);
	}

	@Test
	void 회원탈퇴시_이유와_함께_WithdrawService가_호출된다() {
		// given
		Long userId = 1L;
		WithdrawRequest request = new WithdrawRequest(
			"test-refresh-token",
			Arrays.asList("서비스가 마음에 들지 않아요"),
			"사용성이 불편해요"
		);

		doNothing().when(withdrawService).withdraw(anyLong(), any(WithdrawRequest.class));

		// when
		authService.withdraw(userId, request);

		// then
		verify(withdrawService).withdraw(userId, request);
	}
}
