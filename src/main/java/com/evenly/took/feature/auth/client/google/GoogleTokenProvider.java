package com.evenly.took.feature.auth.client.google;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.google.dto.request.GoogleTokenRequest;
import com.evenly.took.feature.auth.client.google.dto.response.GoogleTokenResponse;
import com.evenly.took.feature.auth.client.google.error.GoogleTokenProviderErrorHandler;
import com.evenly.took.feature.auth.config.properties.GoogleProperties;
import com.evenly.took.feature.auth.config.properties.GoogleUrlProperties;

@Component
public class GoogleTokenProvider {

	private final RestClient restClient;
	private final GoogleProperties googleProperties;
	private final GoogleUrlProperties googleUrlProperties;

	public GoogleTokenProvider(GoogleProperties googleProperties,
		GoogleUrlProperties googleUrlProperties,
		RestClient.Builder restClientBuilder,
		GoogleTokenProviderErrorHandler errorHandler) {

		this.googleProperties = googleProperties;
		this.googleUrlProperties = googleUrlProperties;
		this.restClient = restClientBuilder.clone()
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
				StandardCharsets.UTF_8.name())
			.build();
	}

	public GoogleTokenResponse fetchAccessToken(String authCode) {
		GoogleTokenRequest request = GoogleTokenRequest.of(googleProperties, authCode);
		return restClient.post()
			.uri(googleUrlProperties.tokenUri())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.body(request.toMultiValueMap())
			.retrieve()
			.body(GoogleTokenResponse.class);
	}
}
