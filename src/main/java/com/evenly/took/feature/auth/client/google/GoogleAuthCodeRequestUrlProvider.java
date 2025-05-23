package com.evenly.took.feature.auth.client.google;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.evenly.took.feature.auth.client.AuthCodeRequestUrlProvider;
import com.evenly.took.feature.auth.config.properties.GoogleProperties;
import com.evenly.took.feature.auth.config.properties.GoogleUrlProperties;
import com.evenly.took.feature.auth.domain.OAuthType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

	private final GoogleProperties googleProperties;
	private final GoogleUrlProperties googleUrlProperties;

	@Override
	public OAuthType supportType() {
		return OAuthType.GOOGLE;
	}

	@Override
	public String provide() {
		return UriComponentsBuilder.fromUriString(googleUrlProperties.authorizationUri())
			.queryParam("client_id", googleProperties.clientId())
			.queryParam("redirect_uri", googleProperties.redirectUri())
			.queryParam("response_type", "code")
			.queryParam("scope", googleProperties.scope())
			.queryParam("access_type", "offline")
			.build(true)
			.toUriString();
	}
}
