package com.evenly.took.global.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.notification.domain.Notification;
import com.evenly.took.feature.notification.domain.NotificationType;
import com.evenly.took.feature.user.domain.User;

public abstract class NotificationBase {

	static Long DEFAULT_ID = 1L;
	static User DEFAULT_USER = null;
	static NotificationType DEFAULT_NOTIFICATION_TYPE = NotificationType.MEMO;
	static LocalDateTime DEFAULT_SEND_AT = null;

	Long id;
	User user;
	NotificationType type;
	LocalDateTime sendAt;

	protected NotificationBase() {
		init();
	}

	protected void init() {
		this.id = DEFAULT_ID;
		this.user = DEFAULT_USER;
		this.type = DEFAULT_NOTIFICATION_TYPE;
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

	public NotificationBase type(NotificationType type) {
		this.type = type;
		return this;
	}

	public NotificationBase sendAt(LocalDateTime sendAt) {
		this.sendAt = sendAt;
		return this;
	}

	public abstract Notification create();
}
