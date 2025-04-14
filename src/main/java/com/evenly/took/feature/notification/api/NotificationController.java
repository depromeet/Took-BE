package com.evenly.took.feature.notification.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.notification.application.NotificationService;
import com.evenly.took.feature.notification.dto.NotificationsResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.meta.LoginUser;
import com.evenly.took.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

	private final NotificationService notificationService;

	@GetMapping("/api/notification")
	public SuccessResponse<NotificationsResponse> getNotification(@LoginUser User user) {
		NotificationsResponse response = notificationService.findNotifications(user);
		return SuccessResponse.of(response);
	}
}
