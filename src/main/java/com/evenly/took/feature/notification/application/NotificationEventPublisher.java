package com.evenly.took.feature.notification.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.notification.domain.UserNotification;
import com.evenly.took.feature.notification.event.SendNotificationEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationEventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	public void publishSendNotificationEvent(List<UserNotification> notifications) {
		eventPublisher.publishEvent(new SendNotificationEvent(this, notifications));
	}
}
