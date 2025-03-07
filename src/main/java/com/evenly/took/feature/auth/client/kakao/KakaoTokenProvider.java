package com.evenly.took.feature.auth.client.kakao;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.kakao.dto.request.KakaoTokenRequest;
import com.evenly.took.feature.auth.client.kakao.dto.response.KakaoTokenResponse;
import com.evenly.took.feature.auth.client.kakao.error.KakaoTokenProviderErrorHandler;
import com.evenly.took.feature.auth.config.properties.KakaoProperties;

@Component
public class KakaoTokenProvider {

	private final RestClient restClient;
	private final KakaoProperties kakaoProperties;

	public KakaoTokenProvider(RestClient.Builder restClientBuilder,
		KakaoTokenProviderErrorHandler errorHandler,
		KakaoProperties kakaoProperties) {

		this.restClient = restClientBuilder.clone()
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
				StandardCharsets.UTF_8.name())
			.build();
		this.kakaoProperties = kakaoProperties;
	}

	public KakaoTokenResponse fetchAccessToken(String authCode) {
		KakaoTokenRequest request = KakaoTokenRequest.of(kakaoProperties, authCode);
		return restClient.post()
			.uri(kakaoProperties.url().tokenUrl())
			.body(request.toMultiValueMap())
			.retrieve()
			.body(KakaoTokenResponse.class);
	}
}
