package com.evenly.took.feature.user.api;

import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.dto.response.NearbyUserProfileListResponse;
import com.evenly.took.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "[3. User]")
public interface UserApi {

	@Operation(
		summary = "주변 유저 조회",
		description = "`x-redis-geo` 헤더로 전달된 위치 정보를 기반으로 반경 내의 유저 정보를 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주변 유저 조회 성공")
	})
	SuccessResponse<NearbyUserProfileListResponse> getNearbyUsers(
		@Parameter(description = "로그인한 사용자", hidden = true) User user,
		HttpServletRequest request
	);
}
