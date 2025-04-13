package com.evenly.took.feature.user.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.user.application.UserService;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.dto.request.AllowNotificationRequest;
import com.evenly.took.feature.user.dto.response.AllowNotificationResponse;
import com.evenly.took.global.auth.meta.LoginUser;
import com.evenly.took.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

	private final UserService userService;

	@GetMapping("/api/user/notification-allow")
	public SuccessResponse<AllowNotificationResponse> getAllowNotification(@LoginUser User user) {
		AllowNotificationResponse response = userService.getAllowNotification(user);
		return SuccessResponse.of(response);
	}

	@PutMapping("/api/user/notification-allow")
	public SuccessResponse<Void> fixAllowNotification(
		@LoginUser User user,
		@RequestBody AllowNotificationRequest request) {
		userService.updateAllowNotification(user.getId(), request);
		return SuccessResponse.ok("알림 설정 수정 성공");
	}
}
