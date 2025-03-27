package com.evenly.took.global.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.user.domain.User;

public abstract class FolderBase {

	static Long DEFAULT_ID = 1L;
	static User DEFAULT_USER = null;
	static String DEFAULT_NAME = "기본 폴더";
	static LocalDateTime DEFAULT_DELETED_AT = null;

	Long id;
	User user;
	String name;
	LocalDateTime deletedAt;

	protected FolderBase() {
		init();
	}

	protected void init() {
		this.id = DEFAULT_ID;
		this.user = DEFAULT_USER;
		this.name = DEFAULT_NAME;
		this.deletedAt = DEFAULT_DELETED_AT;
	}

	public FolderBase id(Long id) {
		this.id = id;
		return this;
	}

	public FolderBase user(User user) {
		this.user = user;
		return this;
	}

	public FolderBase name(String name) {
		this.name = name;
		return this;
	}

	public FolderBase deletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
		return this;
	}

	public abstract Folder create();
}
