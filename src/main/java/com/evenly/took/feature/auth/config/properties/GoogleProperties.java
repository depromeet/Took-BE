package com.evenly.took.feature.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
public record GoogleProperties(
	String redirectUri,
	String clientId,
	String clientSecret,
	String scope
) {
}
