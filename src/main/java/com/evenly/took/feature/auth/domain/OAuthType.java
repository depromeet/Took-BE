package com.evenly.took.feature.auth.domain;

public enum OAuthType {

	GOOGLE, KAKAO, APPLE;

	public static OAuthType fromName(String name) {
		return OAuthType.valueOf(name.toUpperCase());
	}
}
