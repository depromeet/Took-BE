package com.evenly.took.feature.notification.application;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.evenly.took.feature.notification.event.SendNotificationEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationEventListener {

	public static final int MINUTES_15 = 900_000;
	private final ExpoNotificationService expoNotificationService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async("NotificationAsyncExecutor")
	public void handleSendNotificationEvent(SendNotificationEvent event) throws InterruptedException {
		List<String> ticketIds = expoNotificationService.sendNotifications(event.getNotifications());
		pause(MINUTES_15);
		expoNotificationService.checkNotifications(ticketIds);
	}

	private void pause(int pauseTimeMs) throws InterruptedException {
		Thread.sleep(pauseTimeMs);
	}
}
