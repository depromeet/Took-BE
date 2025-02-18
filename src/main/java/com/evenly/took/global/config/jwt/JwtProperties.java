package com.evenly.took.global.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
	String accessTokenSecret,
	Long accessTokenExpirationTime
) {

	public Long accessTokenExpirationMilliTime() {
		return accessTokenExpirationTime * 1000;
	}
}
