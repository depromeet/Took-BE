package com.evenly.took.global.config.properties.auth;

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
