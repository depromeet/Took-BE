package com.evenly.took.feature.auth.client.google;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.google.dto.GoogleTokenResponse;
import com.evenly.took.global.config.properties.auth.GoogleProperties;

@Component
public class GoogleTokenProvider {

	private final RestClient restClient;
	private final GoogleProperties googleProperties;

	public GoogleTokenProvider(GoogleProperties googleProperties,
		RestClient.Builder restClientBuilder,
		GoogleTokenProviderErrorHandler errorHandler) {

		this.googleProperties = googleProperties;
		this.restClient = restClientBuilder
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
				StandardCharsets.UTF_8.name())
			.build();
	}

	public GoogleTokenResponse fetchAccessToken(String authCode) {
		return restClient.post()
			.uri(googleProperties.tokenUri())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.body(getTokenRequestParams(authCode))
			.retrieve()
			.body(GoogleTokenResponse.class);
	}

	private MultiValueMap<String, String> getTokenRequestParams(String authCode) {
		MultiValueMap<String, String> tokenRequestParams = new LinkedMultiValueMap<>();
		tokenRequestParams.add("code", authCode);
		tokenRequestParams.add("client_id", googleProperties.clientId());
		tokenRequestParams.add("client_secret", googleProperties.clientSecret());
		tokenRequestParams.add("redirect_uri", googleProperties.redirectUri());
		tokenRequestParams.add("grant_type", "authorization_code");
		return tokenRequestParams;
	}
}
