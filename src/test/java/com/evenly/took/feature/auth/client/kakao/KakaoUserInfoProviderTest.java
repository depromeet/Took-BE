package com.evenly.took.feature.auth.client.kakao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;

import com.evenly.took.feature.auth.client.kakao.dto.KakaoUserResponse;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.global.config.properties.auth.KakaoProperties;
import com.evenly.took.global.service.BaseRestClientTest;

@RestClientTest({KakaoUserInfoProvider.class, KakaoUserInfoProviderErrorHandler.class})
class KakaoUserInfoProviderTest extends BaseRestClientTest {

	KakaoUserInfoProvider kakaoUserInfoProvider;

	@Autowired
	KakaoUserInfoProviderErrorHandler errorHandler;

	@Autowired
	KakaoProperties kakaoProperties;

	@BeforeEach
	void setUp() {
		kakaoUserInfoProvider = new KakaoUserInfoProvider(restClientBuilder, errorHandler, kakaoProperties);
	}

	@Test
	void 성공적으로_응답을_받은_경우_사용자_정보를_조회할_수_있다() throws IOException {
		// given
		String responseBody = readResourceFile("auth/kakao/success-user-info-response.json");
		configure200MockServer(kakaoProperties.url().userInfoUrl(), responseBody);

		// when
		KakaoUserResponse response = kakaoUserInfoProvider.fetchUserInfo("access-token");

		// then
		assertAll(
			() -> mockServer.verify(),
			() -> assertThat(response.id()).isEqualTo(20250303L) // TODO 기획 결정 후 필드 검증 추가
		);
	}

	@Test
	void 유효하지_않은_토큰으로_사용자_정보_조회를_시도할_경우_예외가_발생한다() throws IOException {
		// given
		String errorResponseBody = readResourceFile("auth/kakao/error-invalid-access-token.json");
		configure401MockServer(kakaoProperties.url().userInfoUrl(), errorResponseBody);

		// when, then
		assertThatThrownBy(() -> kakaoUserInfoProvider.fetchUserInfo("invalid-access-token"))
			.isInstanceOf(TookException.class)
			.hasMessage(AuthErrorCode.KAKAO_INVALID_ACCESS_TOKEN.getMessage());
	}
}
