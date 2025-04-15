package com.evenly.took.feature.notification.dto;

import java.util.List;

import com.evenly.took.feature.notification.domain.Notification;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 목록 조회")
public record NotificationsResponse(
	List<NotificationResponse> notifications
) {

	public static NotificationsResponse from(List<Notification> notifications) {
		List<NotificationResponse> responses = notifications.stream()
			.map(NotificationResponse::from)
			.toList();
		return new NotificationsResponse(responses);
	}
}
