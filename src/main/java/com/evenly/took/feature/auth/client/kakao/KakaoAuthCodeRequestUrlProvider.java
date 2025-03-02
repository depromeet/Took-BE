package com.evenly.took.feature.auth.client.kakao;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.evenly.took.feature.auth.client.AuthCodeRequestUrlProvider;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.global.config.properties.auth.KakaoProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(KakaoProperties.class)
public class KakaoAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

	private final KakaoProperties kakaoProperties;

	@Override
	public OAuthType supportType() {
		return OAuthType.KAKAO;
	}

	@Override
	public String provide() {
		return UriComponentsBuilder.fromUriString(kakaoProperties.url().authCodeUrl())
			.queryParam("client_id", kakaoProperties.clientId())
			.queryParam("redirect_uri", kakaoProperties.redirectUri())
			.queryParam("response_type", "code")
			.toUriString();
	}
}
