package com.evenly.took.feature.auth.client.google;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.google.dto.response.GoogleTokenResponse;
import com.evenly.took.feature.auth.client.google.error.GoogleTokenProviderErrorHandler;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.global.exception.TookException;

public class GoogleTokenProviderTest extends MockGoogleProviderTest {

	@Mock
	private GoogleTokenProviderErrorHandler errorHandler;

	private GoogleTokenProvider googleTokenProvider;

	@BeforeEach
	void setUpTokenProvider() {
		googleTokenProvider =
			new GoogleTokenProvider(googleProperties, googleUrlProperties, restClientBuilder, errorHandler);
	}

	@Nested
	class 성공_케이스 {

		@Test
		void 유효한_인증코드_주어졌을때_토큰_반환() {
			// given
			String authCode = "validAuthCode";
			String tokenUri = "http://dummy-token-uri";
			when(googleUrlProperties.tokenUri()).thenReturn(tokenUri);

			GoogleTokenResponse expectedResponse = new GoogleTokenResponse(
				"dummyAccessToken", 3600, "scope", "Bearer", "dummyIdToken"
			);

			RestClient.RequestBodySpec requestBodySpec = restClient.post().uri(tokenUri);
			when(requestBodySpec.header(anyString(), (String[])any())).thenReturn(requestBodySpec);
			lenient().when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);

			RestClient.ResponseSpec responseSpec = requestBodySpec.retrieve();
			when(responseSpec.body(eq(GoogleTokenResponse.class))).thenReturn(expectedResponse);

			// when
			GoogleTokenResponse actualResponse = googleTokenProvider.fetchAccessToken(authCode);

			// then
			assertThat(actualResponse).isNotNull();
			assertThat(actualResponse.accessToken()).isEqualTo("dummyAccessToken");
		}
	}

	@Nested
	class 실패_케이스 {

		@Test
		void 토큰_조회시_예외발생() {
			// given
			String authCode = "invalidAuthCode";
			String tokenUri = "http://dummy-token-uri";
			when(googleUrlProperties.tokenUri()).thenReturn(tokenUri);

			RestClient.RequestBodySpec requestBodySpec = restClient.post().uri(tokenUri);
			when(requestBodySpec.header(anyString(), (String[])any())).thenReturn(requestBodySpec);
			lenient().when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);

			when(requestBodySpec.retrieve())
				.thenThrow(new TookException(AuthErrorCode.INVALID_GOOGLE_TOKEN_REQUEST));

			// when, then
			assertThatThrownBy(() -> googleTokenProvider.fetchAccessToken(authCode))
				.isInstanceOf(TookException.class)
				.hasFieldOrPropertyWithValue("errorCode", AuthErrorCode.INVALID_GOOGLE_TOKEN_REQUEST);
		}
	}
}
