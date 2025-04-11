package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.notification.domain.Notification;

public class NotificationFactory extends NotificationBase {

	UserFactory userFactory;
	ReceivedCardFactory receivedCardFactory;

	@Override
	public Notification create() {
		if (user == null) {
			user = userFactory.create();
		}
		if (receivedCard == null) {
			receivedCard = receivedCardFactory.create();
		}
		Notification notification = Notification.builder()
			.user(user)
			.receivedCard(receivedCard)
			.type(type)
			.willSendAt(willSendAt)
			.build();
		ReflectionTestUtils.setField(notification, "id", id);
		return notification;
	}
}
