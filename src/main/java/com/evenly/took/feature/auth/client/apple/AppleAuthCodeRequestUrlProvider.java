package com.evenly.took.feature.auth.client.apple;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.client.AuthCodeRequestUrlProvider;
import com.evenly.took.feature.auth.client.apple.dto.request.AppleAuthRequest;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.global.config.properties.auth.AppleProperties;
import com.evenly.took.global.config.properties.auth.AppleUrlProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppleAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

	private final AppleProperties appleProperties;
	private final AppleUrlProperties appleUrlProperties;

	@Override
	public OAuthType supportType() {
		return OAuthType.APPLE;
	}

	@Override
	public String provide() {
		AppleAuthRequest request = AppleAuthRequest.of(appleProperties);
		return appleUrlProperties.authUrl() + "?" + request.toQueryString();
	}

}
