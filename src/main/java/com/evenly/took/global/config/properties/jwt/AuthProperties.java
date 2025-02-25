package com.evenly.took.global.config.properties.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
	String accessTokenSecret,
	Long accessTokenExpirationTime,
	Long refreshTokenExpirationDay
) {

	public Long accessTokenExpirationMilliTime() {
		return accessTokenExpirationTime * 1000;
	}
}
