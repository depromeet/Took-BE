package com.evenly.took.feature.notification.api;

import com.evenly.took.feature.notification.dto.NotificationsResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[4. Notification]")
public interface NotificationApi {

	@Operation(
		summary = "알림 목록 조회",
		description = "알림 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
	})
	SuccessResponse<NotificationsResponse> getNotification(User user);
}
