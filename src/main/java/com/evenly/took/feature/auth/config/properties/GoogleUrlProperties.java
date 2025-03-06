package com.evenly.took.feature.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google.url")
public record GoogleUrlProperties(
	String authorizationUri,
	String tokenUri,
	String userInfoUrl
) {
}
