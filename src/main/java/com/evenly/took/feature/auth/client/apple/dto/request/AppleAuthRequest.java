package com.evenly.took.feature.auth.client.apple.dto.request;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.evenly.took.global.config.properties.auth.AppleProperties;

public record AppleAuthRequest(
	String responseType,
	String clientId,
	String redirectUri,
	String state,
	String scope,
	String responseMode
) {

	public static AppleAuthRequest of(AppleProperties appleProperties) {
		return new AppleAuthRequest(
			"code",
			appleProperties.clientId(),
			appleProperties.redirectUri(),
			null,
			"name+email",
			"form_post"
		);
	}

	public static AppleAuthRequest of(AppleProperties appleProperties, String state) {
		return new AppleAuthRequest(
			"code",
			appleProperties.clientId(),
			appleProperties.redirectUri(),
			state,
			"name email",
			"form_post"
		);
	}

	public String toQueryString() {
		return Stream.of(
				paramIfNotNull("response_type", responseType),
				paramIfNotNull("client_id", clientId),
				paramIfNotNull("redirect_uri", redirectUri),
				paramIfNotNull("state", state),
				paramIfNotNull("scope", scope),
				paramIfNotNull("response_mode", responseMode)
			)
			.filter(s -> !s.isEmpty())
			.collect(Collectors.joining("&"));
	}

	private String paramIfNotNull(String key, String value) {
		return value != null ? key + "=" + value : "";
	}
}
