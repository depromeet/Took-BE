package com.evenly.took.feature.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserNotification {

	private final NotificationToken token;
	private final NotificationData data;

	public UserNotification(String token, NotificationType type) {
		this(new NotificationToken(token), NotificationData.from(type));
	}

	public String getToken() {
		return token.getValue();
	}
}
