package com.evenly.took.feature.auth.client.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.evenly.took.global.config.properties.auth.KakaoProperties;

public record KakaoTokenRequest(
	String grantType,
	String clientId,
	String redirectUri,
	String code
) {

	public static KakaoTokenRequest of(KakaoProperties kakaoProperties, String code) {
		return new KakaoTokenRequest("authorization_code",
			kakaoProperties.clientId(),
			kakaoProperties.redirectUri(),
			code);
	}

	public MultiValueMap<String, Object> toMultiValueMap() {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("grant_type", grantType);
		map.add("client_id", clientId);
		map.add("redirect_uri", redirectUri);
		map.add("code", code);
		return map;
	}
}
