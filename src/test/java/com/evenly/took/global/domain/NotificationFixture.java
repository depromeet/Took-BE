package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.notification.dao.NotificationRepository;
import com.evenly.took.feature.notification.domain.Notification;

@Component
public class NotificationFixture extends NotificationBase {

	@Autowired
	NotificationRepository notificationRepository;

	public NotificationBase creator() {
		init();
		return this;
	}

	@Override
	public Notification create() {
		if (user == null) {
			throw new IllegalStateException("user를 함께 입력해주세요.");
		}
		Notification notification = Notification.builder()
			.user(user)
			.type(type)
			.build();
		return notificationRepository.save(notification);
	}

	public long count() {
		return notificationRepository.count();
	}
}
