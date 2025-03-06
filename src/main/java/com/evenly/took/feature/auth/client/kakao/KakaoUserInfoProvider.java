package com.evenly.took.feature.auth.client.kakao;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.kakao.dto.response.KakaoUserResponse;
import com.evenly.took.feature.auth.client.kakao.error.KakaoUserInfoProviderErrorHandler;
import com.evenly.took.feature.auth.config.properties.KakaoProperties;

@Component
public class KakaoUserInfoProvider {

	private final RestClient restClient;
	private final KakaoProperties kakaoProperties;

	public KakaoUserInfoProvider(RestClient.Builder restClientBuilder,
		KakaoUserInfoProviderErrorHandler errorHandler,
		KakaoProperties kakaoProperties) {

		this.restClient = restClientBuilder
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
				StandardCharsets.UTF_8.name())
			.build();
		this.kakaoProperties = kakaoProperties;
	}

	public KakaoUserResponse fetchUserInfo(String accessToken) {
		return restClient.post()
			.uri(kakaoProperties.url().userInfoUrl())
			.header(HttpHeaders.AUTHORIZATION, authHeaderValue(accessToken))
			.retrieve()
			.body(KakaoUserResponse.class);
	}

	private String authHeaderValue(String accessToken) {
		return "Bearer %s".formatted(accessToken);
	}
}
