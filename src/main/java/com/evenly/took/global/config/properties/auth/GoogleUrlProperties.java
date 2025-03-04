package com.evenly.took.global.config.properties.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google.url")
public record GoogleUrlProperties(
	String authorizationUri,
	String tokenUri,
	String userInfoUrl
) {
}
