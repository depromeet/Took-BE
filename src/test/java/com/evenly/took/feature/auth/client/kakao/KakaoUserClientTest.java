package com.evenly.took.feature.auth.client.kakao;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;

import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.global.config.properties.auth.KakaoProperties;
import com.evenly.took.global.service.BaseRestClientTest;

@RestClientTest({KakaoUserClient.class, KakaoResponseErrorHandler.class})
class KakaoUserClientTest extends BaseRestClientTest {

	KakaoUserClient kakaoUserClient;

	@Autowired
	KakaoResponseErrorHandler errorHandler;

	@Autowired
	KakaoProperties kakaoProperties;

	@BeforeEach
	void setUp() {
		mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
		kakaoUserClient = new KakaoUserClient(restClientBuilder, errorHandler, kakaoProperties);
	}

	@Test
	void 유효하지_않은_애플리케이션_정보로_요청을_시도할_경우_예외가_발생한다() {
		// given
		configure401MockServer(kakaoProperties.url().tokenUrl());

		// when, then
		assertThatThrownBy(() -> kakaoUserClient.fetch("valid-code"))
			.isInstanceOf(TookException.class)
			.hasMessage(AuthErrorCode.KAKAO_INVALID_APP_INFO.getMessage());
	}

	@Test
	void 유효하지_않은_인증_코드로_토큰_발급을_시도할_경우_예외가_발생한다() throws IOException {
		// given
		String errorResponseBody = readResourceFile("auth/kakao/error-invalid-code.json");
		configure400MockServer(kakaoProperties.url().tokenUrl(), errorResponseBody);

		// when, then
		assertThatThrownBy(() -> kakaoUserClient.fetch("invalid-code"))
			.isInstanceOf(TookException.class)
			.hasMessage(AuthErrorCode.KAKAO_INVALID_AUTH_CODE.getMessage());
	}
}
