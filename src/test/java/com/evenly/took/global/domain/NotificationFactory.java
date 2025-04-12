package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.notification.domain.Notification;

public class NotificationFactory extends NotificationBase {

	UserFactory userFactory = new UserFactory();

	@Override
	public Notification create() {
		if (user == null) {
			user = userFactory.create();
		}
		Notification notification = Notification.builder()
			.user(user)
			.type(type)
			.build();
		ReflectionTestUtils.setField(notification, "id", id);
		return notification;
	}
}
