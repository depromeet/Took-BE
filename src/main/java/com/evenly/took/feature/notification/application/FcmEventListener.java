package com.evenly.took.feature.notification.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.evenly.took.feature.notification.event.FcmSendEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class FcmEventListener {

	private final FcmService fcmService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async
	public void handleFcmSendEvent(FcmSendEvent event) {
		fcmService.sendFcm(event.getFcmNotifications());
	}
}
