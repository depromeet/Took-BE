package com.evenly.took.global.config.properties.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.apple.url")
public record AppleUrlProperties(
	String tokenUrl,
	String authUrl,
	String revokeUrl
) {
}
