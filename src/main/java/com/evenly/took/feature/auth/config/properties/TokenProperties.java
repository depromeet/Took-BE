package com.evenly.took.feature.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record TokenProperties(
	String accessTokenSecret,
	Long accessTokenExpirationTime,
	Long refreshTokenExpirationTime
) {

	public Long accessTokenExpirationMilliTime() {
		return accessTokenExpirationTime * 1000;
	}
}
