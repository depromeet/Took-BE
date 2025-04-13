package com.evenly.took.feature.auth.client.kakao.dto.request;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.evenly.took.feature.auth.config.properties.KakaoProperties;

public record KakaoTokenRequest(
	String grantType,
	String clientId,
	String clientSecret,
	String redirectUri,
	String code
) {

	public static KakaoTokenRequest of(KakaoProperties kakaoProperties, String code) {
		return new KakaoTokenRequest("authorization_code",
			kakaoProperties.clientId(),
			kakaoProperties.clientSecret(),
			kakaoProperties.redirectUri(),
			code);
	}

	public MultiValueMap<String, Object> toMultiValueMap() {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("grant_type", grantType);
		map.add("client_id", clientId);
		map.add("client_secret", clientSecret);
		map.add("redirect_uri", redirectUri);
		map.add("code", code);
		return map;
	}
}
