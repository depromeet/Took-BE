package com.evenly.took.global.config.properties.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.apple")
public record AppleProperties(
	String clientId,
	String redirectUri,
	String teamId,
	String keyId,
	String privateKey
) {
}
