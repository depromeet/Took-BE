package com.evenly.took.feature.auth.client.google;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.google.dto.GoogleUserInfoResponse;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.global.config.properties.auth.GoogleProperties;
import com.evenly.took.global.service.MockTest;

public class GoogleUserInfoProviderTest extends MockTest {

	@Mock
	private GoogleProperties googleProperties;

	@Mock
	private RestClient.Builder restClientBuilder;

	@Mock
	private GoogleUserInfoProviderErrorHandler errorHandler;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private RestClient restClient;

	private GoogleUserInfoProvider googleUserInfoProvider;

	@BeforeEach
	void setUp() {
		when(restClientBuilder.defaultStatusHandler(errorHandler)).thenReturn(restClientBuilder);
		when(restClientBuilder.defaultHeader("Content-Type", "application/x-www-form-urlencoded", "UTF-8"))
			.thenReturn(restClientBuilder);
		when(restClientBuilder.build()).thenReturn(restClient);

		googleUserInfoProvider = new GoogleUserInfoProvider(googleProperties, restClientBuilder, errorHandler);
	}

	@Nested
	class 성공_케이스 {
		@Test
		void 유효한_액세스토큰_주어졌을때_사용자정보_반환() {
			// given
			String accessToken = "validAccessToken";
			String userInfoUrl = "http://dummy-userinfo-url";
			when(googleProperties.userInfoUrl()).thenReturn(userInfoUrl);

			GoogleUserInfoResponse expectedResponse = new GoogleUserInfoResponse("dummySub", "홍길동");

			RestClient.RequestHeadersSpec requestSpec = restClient.get().uri(userInfoUrl);
			when(requestSpec.header("Authorization", "Bearer " + accessToken)).thenReturn(requestSpec);
			RestClient.ResponseSpec responseSpec = requestSpec.retrieve();
			when(responseSpec.body(eq(GoogleUserInfoResponse.class))).thenReturn(expectedResponse);

			// when
			GoogleUserInfoResponse actualResponse = googleUserInfoProvider.fetchUserInfo(accessToken);

			// then
			assertThat(actualResponse).isNotNull();
			assertThat(actualResponse.sub()).isEqualTo("dummySub");
			assertThat(actualResponse.name()).isEqualTo("홍길동");
		}
	}

	@Nested
	class 실패_케이스 {
		@Test
		void 사용자정보_조회시_예외발생() {
			// given
			String accessToken = "invalidAccessToken";
			String userInfoUrl = "http://dummy-userinfo-url";
			when(googleProperties.userInfoUrl()).thenReturn(userInfoUrl);

			RestClient.RequestHeadersSpec requestSpec = restClient.get().uri(userInfoUrl);
			when(requestSpec.header("Authorization", "Bearer " + accessToken)).thenReturn(requestSpec);
			RestClient.ResponseSpec responseSpec = requestSpec.retrieve();
			when(responseSpec.body(eq(GoogleUserInfoResponse.class)))
				.thenThrow(new TookException(AuthErrorCode.INVALID_GOOGLE_USER_REQUEST));

			// when, then
			assertThatThrownBy(() -> googleUserInfoProvider.fetchUserInfo(accessToken))
				.isInstanceOf(TookException.class)
				.hasFieldOrPropertyWithValue("errorCode", AuthErrorCode.INVALID_GOOGLE_USER_REQUEST);
		}
	}
}
