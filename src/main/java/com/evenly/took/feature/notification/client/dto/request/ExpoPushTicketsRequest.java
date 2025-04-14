package com.evenly.took.feature.notification.client.dto.request;

import com.evenly.took.feature.notification.domain.UserNotification;

public record ExpoPushTicketsRequest(
	String to,
	String sound,
	String title,
	String body,
	ExpoPushTicketsDataRequest data
) {

	public ExpoPushTicketsRequest(UserNotification notification) {
		this(notification.getToken(),
			"default",
			notification.getData().getTitle(),
			notification.getData().getBody(),
			new ExpoPushTicketsDataRequest(notification.getData().getLink()));
	}
}
