package com.evenly.took.feature.auth.client.google;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.google.dto.GoogleUserInfoResponse;
import com.evenly.took.global.config.properties.auth.GoogleProperties;

@Component
public class GoogleUserInfoProvider {

	private final RestClient restClient;
	private final GoogleProperties googleProperties;

	public GoogleUserInfoProvider(GoogleProperties googleProperties,
		RestClient.Builder restClientBuilder,
		GoogleUserInfoProviderErrorHandler errorHandler) {

		this.googleProperties = googleProperties;
		this.restClient = restClientBuilder
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
				StandardCharsets.UTF_8.name())
			.build();
	}

	public GoogleUserInfoResponse fetchUserInfo(String accessToken) {
		return restClient.get()
			.uri(googleProperties.userInfoUrl())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.body(GoogleUserInfoResponse.class);
	}
}
