package com.evenly.took.global.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.notification.domain.Notification;
import com.evenly.took.feature.notification.domain.NotificationType;
import com.evenly.took.feature.user.domain.User;

public abstract class NotificationBase {

	static Long DEFAULT_ID = 1L;
	static User DEFAULT_USER = null;
	static ReceivedCard DEFAULT_RECEIVED_CARD = null;
	static NotificationType DEFAULT_NOTIFICATION_TYPE = NotificationType.MEMO;
	static LocalDateTime DEFAULT_WILL_SEND_AT = LocalDateTime.now();
	static LocalDateTime DEFAULT_SEND_AT = null;

	Long id;
	User user;
	ReceivedCard receivedCard;
	NotificationType type;
	LocalDateTime willSendAt;
	LocalDateTime sendAt;

	protected NotificationBase() {
		init();
	}

	protected void init() {
		this.id = DEFAULT_ID;
		this.user = DEFAULT_USER;
		this.receivedCard = DEFAULT_RECEIVED_CARD;
		this.type = DEFAULT_NOTIFICATION_TYPE;
		this.willSendAt = DEFAULT_WILL_SEND_AT;
		this.sendAt = DEFAULT_SEND_AT;
	}

	public NotificationBase id(Long id) {
		this.id = id;
		return this;
	}

	public NotificationBase user(User user) {
		this.user = user;
		return this;
	}

	public NotificationBase receivedCard(ReceivedCard receivedCard) {
		this.receivedCard = receivedCard;
		return this;
	}

	public NotificationBase type(NotificationType type) {
		this.type = type;
		return this;
	}

	public NotificationBase willSendAt(LocalDateTime willSendAt) {
		this.willSendAt = willSendAt;
		return this;
	}

	public NotificationBase sendAt(LocalDateTime sendAt) {
		this.sendAt = sendAt;
		return this;
	}

	public abstract Notification create();
}
