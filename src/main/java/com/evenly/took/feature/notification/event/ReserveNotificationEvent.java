package com.evenly.took.feature.notification.event;

import org.springframework.context.ApplicationEvent;

import com.evenly.took.feature.card.domain.ReceivedCard;

import lombok.Getter;

@Getter
public class ReserveNotificationEvent extends ApplicationEvent {

	private final ReceivedCard receivedCard;

	public ReserveNotificationEvent(Object source, ReceivedCard receivedCard) {
		super(source);
		this.receivedCard = receivedCard;
	}
}
