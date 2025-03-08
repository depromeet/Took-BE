package com.evenly.took.feature.auth.client.apple;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.client.apple.dto.response.AppleTokenResponse;
import com.evenly.took.feature.auth.client.apple.dto.response.AppleUserResponse;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.MockTest;

class AppleUserClientTest extends MockTest {

	@Mock
	private AppleTokenProvider appleTokenProvider;

	@Mock
	private AppleUserInfoProvider appleUserInfoProvider;

	@InjectMocks
	private AppleUserClient appleUserClient;

	@Nested
	class 사용자_정보_조회_테스트 {

		private static final String AUTH_CODE = "test_auth_code";
		private static final String ID_TOKEN = "test_id_token";

		@Test
		void 로그인_성공() {
			// Arrange
			AuthContext authContext = new AuthContext(AUTH_CODE);
			AppleTokenResponse mockTokenResponse = mock(AppleTokenResponse.class);
			AppleUserResponse mockUserResponse = new AppleUserResponse("apple_user_id", null, "user@example.com");

			when(appleTokenProvider.fetchIdToken(AUTH_CODE)).thenReturn(mockTokenResponse);
			when(mockTokenResponse.idToken()).thenReturn(ID_TOKEN);
			when(appleUserInfoProvider.fetchLoginUser(ID_TOKEN)).thenReturn(mockUserResponse);

			// Act
			User resultUser = appleUserClient.fetch(authContext);

			// Assert
			assertNotNull(resultUser);
			assertEquals("user@example.com", resultUser.getName());
			assertEquals(OAuthType.APPLE, resultUser.getOauthIdentifier().getOauthType());
			assertEquals("apple_user_id", resultUser.getOauthIdentifier().getOauthId());

			// Verify
			verify(appleTokenProvider).fetchIdToken(AUTH_CODE);
			verify(appleUserInfoProvider).fetchLoginUser(ID_TOKEN);
		}

		@Test
		void 회원가입_성공() {
			// Arrange
			String name = "테스트";
			AuthContext authContext = new AuthContext(AUTH_CODE, name);
			AppleTokenResponse mockTokenResponse = mock(AppleTokenResponse.class);
			AppleUserResponse mockUserResponse = new AppleUserResponse("apple_user_id", name, "user@example.com");

			when(appleTokenProvider.fetchIdToken(AUTH_CODE)).thenReturn(mockTokenResponse);
			when(mockTokenResponse.idToken()).thenReturn(ID_TOKEN);
			when(appleUserInfoProvider.fetchSignupUser(ID_TOKEN, name)).thenReturn(mockUserResponse);

			// Act
			User resultUser = appleUserClient.fetch(authContext);

			// Assert
			assertNotNull(resultUser);
			assertEquals(name, resultUser.getName());
			assertEquals(OAuthType.APPLE, resultUser.getOauthIdentifier().getOauthType());
			assertEquals("apple_user_id", resultUser.getOauthIdentifier().getOauthId());

			// Verify
			verify(appleTokenProvider).fetchIdToken(AUTH_CODE);
			verify(appleUserInfoProvider).fetchSignupUser(ID_TOKEN, name);
		}

		@Test
		void 토큰_제공_실패_예외_처리() {
			// Arrange
			AuthContext authContext = new AuthContext(AUTH_CODE);

			when(appleTokenProvider.fetchIdToken(AUTH_CODE))
				.thenThrow(new TookException(AuthErrorCode.APPLE_SERVER_ERROR));

			// Act & Assert
			TookException exception = assertThrows(TookException.class, () -> {
				appleUserClient.fetch(authContext);
			});

			assertEquals(AuthErrorCode.APPLE_SERVER_ERROR, exception.getErrorCode());
		}
	}

	@Test
	void OAuth_타입_확인() {
		assertEquals(OAuthType.APPLE, appleUserClient.supportType());
	}
}
