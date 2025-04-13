package com.evenly.took.feature.notification.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.evenly.took.feature.notification.client.ExpoMessageSender;
import com.evenly.took.feature.notification.domain.UserNotification;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ExpoNotificationService {

	private static final int MAX_NOTIFICATION_COUNT = 100;

	private final ExpoMessageSender messageSender;

	public void sendNotification(List<UserNotification> notifications) {
		if (notifications == null || notifications.isEmpty()) {
			return;
		}
		int totalSize = notifications.size();
		for (int i = 0; i < totalSize; i += MAX_NOTIFICATION_COUNT) {
			List<UserNotification> batch = notifications.subList(i, Math.min(i + MAX_NOTIFICATION_COUNT, totalSize));
			messageSender.send(batch);
		}
		// TODO 비동기 결과 처리
	}
}
