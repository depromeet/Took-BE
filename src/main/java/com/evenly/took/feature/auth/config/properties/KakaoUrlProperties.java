package com.evenly.took.feature.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao.url")
public record KakaoUrlProperties(
	String authCodeUrl,
	String tokenUrl,
	String userInfoUrl
) {
}
