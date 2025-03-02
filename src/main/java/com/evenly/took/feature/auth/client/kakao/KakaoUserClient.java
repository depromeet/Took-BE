package com.evenly.took.feature.auth.client.kakao;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.UserClient;
import com.evenly.took.feature.auth.client.kakao.dto.KakaoTokenRequest;
import com.evenly.took.feature.auth.client.kakao.dto.KakaoTokenResponse;
import com.evenly.took.feature.auth.client.kakao.dto.KakaoUserResponse;
import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.config.properties.auth.KakaoProperties;

@Component
@EnableConfigurationProperties(KakaoProperties.class)
public class KakaoUserClient implements UserClient {

	private final RestClient restClient;
	private final KakaoProperties kakaoProperties;

	public KakaoUserClient(RestClient.Builder restClientBuilder,
		KakaoResponseErrorHandler errorHandler,
		KakaoProperties kakaoProperties) {

		this.restClient = restClientBuilder
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_FORM_URLENCODED_VALUE,
				StandardCharsets.UTF_8.name())
			.build();
		this.kakaoProperties = kakaoProperties;
	}

	@Override
	public OAuthType supportType() {
		return OAuthType.KAKAO;
	}

	@Override
	public User fetch(String authCode) {
		String accessToken = fetchAccessToken(authCode);
		KakaoUserResponse response = restClient.post()
			.uri(kakaoProperties.url().userInfoUrl())
			.header(HttpHeaders.AUTHORIZATION, authHeaderValue(accessToken))
			.retrieve()
			.body(KakaoUserResponse.class);
		return buildUser(response);
	}

	private String fetchAccessToken(String authCode) {
		KakaoTokenRequest request = KakaoTokenRequest.of(kakaoProperties, authCode);
		KakaoTokenResponse response = restClient.post()
			.uri(kakaoProperties.url().tokenUrl())
			.body(request.toMultiValueMap())
			.retrieve()
			.body(KakaoTokenResponse.class);
		return response.accessToken();
	}

	private String authHeaderValue(String accessToken) {
		return "Bearer %s".formatted(accessToken);
	}

	private User buildUser(KakaoUserResponse response) {
		OAuthIdentifier oAuthIdentifier = OAuthIdentifier.builder()
			.oauthId(response.id().toString())
			.oauthType(OAuthType.KAKAO)
			.build();
		return User.builder()
			.name("dummy_name") // TODO 기획 결정 후 변경
			.oauthIdentifier(oAuthIdentifier)
			.build();
	}
}
