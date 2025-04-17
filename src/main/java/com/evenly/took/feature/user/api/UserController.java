package com.evenly.took.feature.user.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.user.application.FindNearbyUserService;
import com.evenly.took.feature.user.application.UserService;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.dto.request.AllowNotificationRequest;
import com.evenly.took.feature.user.dto.response.AllowNotificationResponse;
import com.evenly.took.feature.user.dto.response.NearbyUserProfileListResponse;
import com.evenly.took.feature.user.dto.response.NearbyUserProfileResponse;
import com.evenly.took.global.auth.meta.LoginUser;
import com.evenly.took.global.location.meta.RegisterLocation;
import com.evenly.took.global.monitoring.slack.SlackErrorAlert;
import com.evenly.took.global.response.SuccessResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SlackErrorAlert
@RestController
@RegisterLocation
@RequiredArgsConstructor
public class UserController implements UserApi {

	private final UserService userService;
	private final FindNearbyUserService findNearbyUserService;

	@GetMapping("/api/user/notification-allow")
	public SuccessResponse<AllowNotificationResponse> getAllowNotification(@LoginUser User user) {
		AllowNotificationResponse response = userService.getAllowNotification(user);
		return SuccessResponse.of(response);
	}

	@PatchMapping("/api/user/notification-allow")
	public SuccessResponse<Void> fixAllowNotification(
		@LoginUser User user,
		@RequestBody AllowNotificationRequest request) {
		userService.updateAllowNotification(user.getId(), request);
		return SuccessResponse.ok("알림 설정 수정 성공");
	}

	@GetMapping("/api/user/nearby")
	public SuccessResponse<NearbyUserProfileListResponse> getNearbyUsers(
		@LoginUser User user,
		HttpServletRequest request
	) {
		List<NearbyUserProfileResponse> profiles = findNearbyUserService.invoke(user.getId(), request);
		NearbyUserProfileListResponse response = new NearbyUserProfileListResponse(profiles);
		return SuccessResponse.of(response);
	}
}
