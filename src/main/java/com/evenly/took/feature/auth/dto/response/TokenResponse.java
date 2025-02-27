package com.evenly.took.feature.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 토큰 정보")
public record TokenResponse(
	@Schema(description = "액세스 토큰 정보") String accessToken,
	@Schema(description = "리프레시 토큰 정보") String refreshToken
) {
}
