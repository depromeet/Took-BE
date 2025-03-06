package com.evenly.took.feature.auth.client.google;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.google.dto.response.GoogleUserInfoResponse;
import com.evenly.took.feature.auth.client.google.error.GoogleUserInfoProviderErrorHandler;
import com.evenly.took.feature.auth.config.properties.GoogleUrlProperties;

@Component
public class GoogleUserInfoProvider {

	private final RestClient restClient;
	private final GoogleUrlProperties googleUrlProperties;

	public GoogleUserInfoProvider(GoogleUrlProperties googleUrlProperties,
		RestClient.Builder restClientBuilder,
		GoogleUserInfoProviderErrorHandler errorHandler) {

		this.googleUrlProperties = googleUrlProperties;
		this.restClient = restClientBuilder.clone()
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
				StandardCharsets.UTF_8.name())
			.build();
	}

	public GoogleUserInfoResponse fetchUserInfo(String accessToken) {
		return restClient.get()
			.uri(googleUrlProperties.userInfoUrl())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.body(GoogleUserInfoResponse.class);
	}
}
