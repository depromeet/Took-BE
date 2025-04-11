package com.evenly.took.feature.card.application;

import org.springframework.context.ApplicationEventPublisher;

import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.notification.event.ReserveNotificationEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationEventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	public void reserveNotification(ReceivedCard receivedCard) {
		eventPublisher.publishEvent(new ReserveNotificationEvent(this, receivedCard));
	}
}
