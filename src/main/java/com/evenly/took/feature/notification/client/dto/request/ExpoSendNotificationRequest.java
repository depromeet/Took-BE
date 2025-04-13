package com.evenly.took.feature.notification.client.dto.request;

import com.evenly.took.feature.notification.domain.UserNotification;

public record ExpoSendNotificationRequest(
	String to,
	String sound,
	String title,
	String body,
	ExpoSendNotificationDataRequest data
) {

	public ExpoSendNotificationRequest(UserNotification notification) {
		this(notification.getToken(),
			"default",
			notification.getData().getTitle(),
			notification.getData().getBody(),
			new ExpoSendNotificationDataRequest(notification.getData().getLink()));
	}
}
