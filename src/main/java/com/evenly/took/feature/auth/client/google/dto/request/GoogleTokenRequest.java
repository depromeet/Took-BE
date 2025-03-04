package com.evenly.took.feature.auth.client.google.dto.request;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.evenly.took.feature.auth.config.properties.GoogleProperties;

public record GoogleTokenRequest(
	String grantType,
	String clientId,
	String redirectUri,
	String code,
	String clientSecret
) {

	public static GoogleTokenRequest of(GoogleProperties googleProperties, String code) {
		return new GoogleTokenRequest("authorization_code",
			googleProperties.clientId(),
			googleProperties.redirectUri(),
			code,
			googleProperties.clientSecret());
	}

	public MultiValueMap<String, Object> toMultiValueMap() {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("grant_type", grantType);
		map.add("client_id", clientId);
		map.add("redirect_uri", redirectUri);
		map.add("code", code);
		map.add("client_secret", clientSecret);
		return map;
	}
}
