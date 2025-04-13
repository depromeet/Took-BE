package com.evenly.took.feature.notification.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.evenly.took.feature.notification.event.SendNotificationEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationEventListener {

	private final ExpoNotificationService expoNotificationService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async
	public void handleSendNotificationEvent(SendNotificationEvent event) {
		expoNotificationService.sendNotification(event.getNotifications());
	}
}
