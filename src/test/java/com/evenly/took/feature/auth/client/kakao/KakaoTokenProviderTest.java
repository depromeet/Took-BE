package com.evenly.took.feature.auth.client.kakao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;

import com.evenly.took.feature.auth.client.kakao.dto.response.KakaoTokenResponse;
import com.evenly.took.feature.auth.client.kakao.error.KakaoTokenProviderErrorHandler;
import com.evenly.took.feature.auth.config.properties.KakaoProperties;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.BaseRestClientTest;

@RestClientTest({KakaoTokenProvider.class,
	KakaoTokenProviderErrorHandler.class,
	KakaoProperties.class})
class KakaoTokenProviderTest extends BaseRestClientTest {

	KakaoTokenProvider kakaoTokenProvider;

	@Autowired
	KakaoTokenProviderErrorHandler errorHandler;

	@Autowired
	KakaoProperties kakaoProperties;

	@BeforeEach
	void setUp() {
		kakaoTokenProvider = new KakaoTokenProvider(restClientBuilder, errorHandler, kakaoProperties);
	}

	@Test
	void 성공적으로_응답을_받은_경우_토큰_정보를_조회할_수_있다() throws IOException {
		// given
		String responseBody = readResourceFile("auth/kakao/success-token-response.json");
		configure200MockServer(kakaoProperties.url().tokenUrl(), responseBody);

		// when
		KakaoTokenResponse response = kakaoTokenProvider.fetchAccessToken("code");

		// then
		assertAll(
			() -> mockServer.verify(),
			() -> assertThat(response.accessToken()).isEqualTo("took-access-token")
		);
	}

	@Test
	void 유효하지_않은_애플리케이션_정보로_요청을_시도할_경우_예외가_발생한다() {
		// given
		configure401MockServer(kakaoProperties.url().tokenUrl());

		// when, then
		assertThatThrownBy(() -> kakaoTokenProvider.fetchAccessToken("code"))
			.isInstanceOf(TookException.class)
			.hasMessage(AuthErrorCode.KAKAO_INVALID_APP_INFO.getMessage());
	}

	@Test
	void 유효하지_않은_인증_코드로_토큰_발급을_시도할_경우_예외가_발생한다() throws IOException {
		// given
		String errorResponseBody = readResourceFile("auth/kakao/error-invalid-code.json");
		configure400MockServer(kakaoProperties.url().tokenUrl(), errorResponseBody);

		// when, then
		assertThatThrownBy(() -> kakaoTokenProvider.fetchAccessToken("invalid-code"))
			.isInstanceOf(TookException.class)
			.hasMessage(AuthErrorCode.KAKAO_INVALID_AUTH_CODE.getMessage());
	}

	@Test
	void 처리하지_않은_에러가_발생할_경우_500_에러를_반환한다() throws IOException {
		// given
		String errorResponseBody = readResourceFile("auth/kakao/error-unhandled.json");
		configure400MockServer(kakaoProperties.url().tokenUrl(), errorResponseBody);

		// when, then
		assertThatThrownBy(() -> kakaoTokenProvider.fetchAccessToken("code"))
			.isInstanceOf(TookException.class)
			.hasMessage(AuthErrorCode.KAKAO_SERVER_ERROR.getMessage());
	}
}
