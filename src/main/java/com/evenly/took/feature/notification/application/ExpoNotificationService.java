package com.evenly.took.feature.notification.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.evenly.took.feature.notification.client.ExpoNotificationSender;
import com.evenly.took.feature.notification.domain.UserNotification;
import com.evenly.took.global.redis.RedisService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ExpoNotificationService {

	private static final int MAX_PUSH_TICKETS_COUNT = 100;
	private static final int MAX_PUSH_RECEIPTS_COUNT = 1000;

	private final ExpoNotificationSender messageSender;
	private final RedisService redisService;

	public List<String> sendNotifications(List<UserNotification> notifications) {
		if (notifications == null || notifications.isEmpty()) {
			return List.of();
		}
		return partition(notifications, MAX_PUSH_TICKETS_COUNT).stream()
			.map(messageSender::pushTickets)
			.flatMap(Collection::stream)
			.toList();
	}

	public void checkNotifications(List<String> ticketIds) {
		if (ticketIds == null || ticketIds.isEmpty()) {
			return;
		}
		List<String> failIds = partition(ticketIds, MAX_PUSH_RECEIPTS_COUNT).stream()
			.map(messageSender::pushReceipts)
			.flatMap(Collection::stream)
			.toList();
		recordFailNotifications(failIds);
	}

	private <T> List<List<T>> partition(List<T> total, int batchSize) {
		List<List<T>> partitions = new ArrayList<>();
		for (int i = 0; i < total.size(); i += batchSize) {
			List<T> partition = total.subList(i, Math.min(i + batchSize, total.size()));
			partitions.add(partition);
		}
		return partitions;
	}

	private void recordFailNotifications(List<String> failIds) {
		if (!failIds.isEmpty()) {
			redisService.setValue("notification-fail-id-%s".formatted(LocalDateTime.now()), failIds.toString());
		}
	}
}
