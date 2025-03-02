package com.evenly.took.global.config.properties.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoProperties(
	String authCodeUrl,
	String clientId,
	String redirectUri
) {
}
