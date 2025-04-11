package com.evenly.took.feature.notification.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.evenly.took.feature.notification.event.ReserveNotificationEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationEventListener {

	private final NotificationService notificationService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async
	public void handleReserveNotificationEvent(ReserveNotificationEvent event) {
		notificationService.reserveNotification(event.getReceivedCard());
	}
}
