package com.evenly.took.global.config.properties.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao.url")
public record KakaoUrlProperties(
	String authCodeUrl,
	String tokenUrl,
	String userInfoUrl
) {
}
