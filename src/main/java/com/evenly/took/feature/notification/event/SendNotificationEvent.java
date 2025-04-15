package com.evenly.took.feature.notification.event;

import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.evenly.took.feature.notification.domain.UserNotification;

public class SendNotificationEvent extends ApplicationEvent {

	private final List<UserNotification> notifications;

	public SendNotificationEvent(Object source, List<UserNotification> notifications) {
		super(source);
		this.notifications = notifications;
	}

	public List<UserNotification> getNotifications() {
		return Collections.unmodifiableList(notifications);
	}
}
