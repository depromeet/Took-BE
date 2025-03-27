package com.evenly.took.global.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.user.domain.User;

public abstract class ReceivedCardBase {

	static Long DEFAULT_ID = 1L;
	static User DEFAULT_USER = null;
	static Card DEFAULT_CARD = null;
	static String DEFAULT_MEMO = null;
	static LocalDateTime DEFAULT_DELETED_AT = null;

	Long id;
	User user;
	Card card;
	String memo;
	LocalDateTime deletedAt;

	protected ReceivedCardBase() {
		init();
	}

	protected void init() {
		this.id = DEFAULT_ID;
		this.user = DEFAULT_USER;
		this.card = DEFAULT_CARD;
		this.memo = DEFAULT_MEMO;
		this.deletedAt = DEFAULT_DELETED_AT;
	}

	public ReceivedCardBase id(Long id) {
		this.id = id;
		return this;
	}

	public ReceivedCardBase user(User user) {
		this.user = user;
		return this;
	}

	public ReceivedCardBase card(Card card) {
		this.card = card;
		return this;
	}

	public ReceivedCardBase memo(String memo) {
		this.memo = memo;
		return this;
	}

	public ReceivedCardBase deletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
		return this;
	}

	public abstract ReceivedCard create();
}
