package com.evenly.took.feature.user.api;

import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.dto.request.AllowNotificationRequest;
import com.evenly.took.feature.user.dto.response.AllowNotificationResponse;
import com.evenly.took.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[3. User]")
public interface UserApi {

	@Operation(
		summary = "알림 설정 조회",
		description = "알림 설정을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "알림 설정 조회 성공")
	})
	SuccessResponse<AllowNotificationResponse> getAllowNotification(User user);

	@Operation(
		summary = "알림 설정 수정",
		description = "알림 설정을 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "알림 설정 수정 성공")
	})
	SuccessResponse<Void> fixAllowNotification(User user, AllowNotificationRequest request);
}
