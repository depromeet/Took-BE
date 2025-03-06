package com.evenly.took.feature.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.apple.url")
public record AppleUrlProperties(
	String tokenUrl,
	String authUrl
) {
}
