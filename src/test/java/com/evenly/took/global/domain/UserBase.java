package com.evenly.took.global.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.card.domain.vo.WithdrawReasons;
import com.evenly.took.feature.user.domain.User;

public abstract class UserBase {

	static Long DEFAULT_ID = 1L;
	static String DEFAULT_NAME = "임손나";
	static String DEFAULT_EMAIL = "took@google.com";
	static OAuthIdentifier DEFAULT_OAUTH_IDENTIFIER = null;
	static LocalDateTime DEFAULT_DELETED_AT = null;
	static WithdrawReasons DEFAULT_WITHDRAW_REASONS = null;

	Long id;
	String name;
	String email;
	OAuthIdentifier oauthIdentifier;
	LocalDateTime deletedAt;
	WithdrawReasons withdrawReasons;

	protected UserBase() {
		init();
	}

	protected void init() {
		this.id = DEFAULT_ID;
		this.name = DEFAULT_NAME;
		this.email = DEFAULT_EMAIL;
		this.oauthIdentifier = DEFAULT_OAUTH_IDENTIFIER;
		this.deletedAt = DEFAULT_DELETED_AT;
		this.withdrawReasons = DEFAULT_WITHDRAW_REASONS;
	}

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

	public UserBase deletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
		return this;
	}

	public UserBase withdrawReasons(WithdrawReasons withdrawReasons) {
		this.withdrawReasons = withdrawReasons;
		return this;
	}

	public abstract User create();
}
