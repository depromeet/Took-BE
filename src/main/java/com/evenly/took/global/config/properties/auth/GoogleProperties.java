package com.evenly.took.global.config.properties.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
public record GoogleProperties(
	String authorizationUri,
	String tokenUri,
	String redirectUri,
	String userInfoUrl,
	String clientId,
	String clientSecret,
	String scope
) {
}
