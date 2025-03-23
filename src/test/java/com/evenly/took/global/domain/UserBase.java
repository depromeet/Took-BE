package com.evenly.took.global.domain;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;

public abstract class UserBase {

	protected Long id = 1L;
	protected String name = "임손나";
	protected String email = "took@google.com";
	protected OAuthIdentifier oauthIdentifier = OAuthIdentifier.builder()
		.oauthId("oauth-id")
		.oauthType(OAuthType.GOOGLE)
		.build();

	public UserBase id(Long id) {
		this.id = id;
		return this;
	}

	public UserBase name(String name) {
		this.name = name;
		return this;
	}

	public UserBase email(String email) {
		this.email = email;
		return this;
	}

	public UserBase oauthIdentifier(OAuthIdentifier oauthIdentifier) {
		this.oauthIdentifier = oauthIdentifier;
		return this;
	}

	public UserBase googleOAuth(String oauthId) {
		return oauthIdentifier(OAuthIdentifier.builder()
			.oauthId(oauthId)
			.oauthType(OAuthType.GOOGLE)
			.build());
	}

	public UserBase kakaoOAuth(String oauthId) {
		return oauthIdentifier(OAuthIdentifier.builder()
			.oauthId(oauthId)
			.oauthType(OAuthType.KAKAO)
			.build());
	}

	public UserBase appleOAuth(String oauthId) {
		return oauthIdentifier(OAuthIdentifier.builder()
			.oauthId(oauthId)
			.oauthType(OAuthType.APPLE)
			.build());
	}

	public abstract User create();
}
