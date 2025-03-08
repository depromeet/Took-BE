package com.evenly.took.feature.auth.client.google;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.client.google.dto.response.GoogleTokenResponse;
import com.evenly.took.feature.auth.client.google.dto.response.GoogleUserInfoResponse;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.MockTest;

public class GoogleUserClientTest extends MockTest {

	@Mock
	private GoogleTokenProvider googleTokenProvider;

	@Mock
	private GoogleUserInfoProvider googleUserInfoProvider;

	@InjectMocks
	private GoogleUserClient googleUserClient;

	@Nested
	class 성공_케이스 {
		@Test
		void 유효한_인증코드_주어졌을때_유저_반환() {
			// given
			String authCode = "validAuthCode";
			GoogleTokenResponse tokenResponse = new GoogleTokenResponse("dummyAccessToken", 3600, "scope", "Bearer",
				"dummyIdToken");
			GoogleUserInfoResponse userInfoResponse = new GoogleUserInfoResponse("dummySub", "홍길동");

			when(googleTokenProvider.fetchAccessToken(authCode)).thenReturn(tokenResponse);
			when(googleUserInfoProvider.fetchUserInfo(tokenResponse.accessToken())).thenReturn(userInfoResponse);

			// when
			User user = googleUserClient.fetch(new AuthContext(authCode));

			// then
			assertThat(user).isNotNull();
			assertThat(user.getName()).isEqualTo("홍길동");
			assertThat(user.getOauthIdentifier().getOauthId()).isEqualTo("dummySub");
			assertThat(user.getOauthIdentifier().getOauthType()).isEqualTo(OAuthType.GOOGLE);
		}
	}

	@Nested
	class 실패_케이스 {
		@Test
		void 잘못된_인증코드_주어졌을때_예외_발생() {
			// given
			String authCode = "invalidAuthCode";
			when(googleTokenProvider.fetchAccessToken(authCode))
				.thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

			// when, then
			assertThatThrownBy(() -> googleUserClient.fetch(new AuthContext(authCode)))
				.isInstanceOf(TookException.class)
				.hasFieldOrPropertyWithValue("errorCode", AuthErrorCode.INVALID_GOOGLE_CONNECTION);
		}
	}
}
