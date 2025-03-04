package com.evenly.took.feature.auth.client.apple.dto.request;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.evenly.took.global.config.properties.auth.AppleProperties;

public record AppleTokenRequest(
	String grantType,
	String clientId,
	String clientSecret,
	String redirectUri,
	String code
) {

	public static AppleTokenRequest of(AppleProperties appleProperties, String code, String clientSecret) {
		return new AppleTokenRequest(
			"authorization_code",
			appleProperties.clientId(),
			clientSecret,
			appleProperties.redirectUri(),
			code
		);
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
