package com.evenly.took.feature.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoProperties(
	String clientId,
	String clientSecret,
	String redirectUri,
	KakaoUrlProperties url
) {
}
