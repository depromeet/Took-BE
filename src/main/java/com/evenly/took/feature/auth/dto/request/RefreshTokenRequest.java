package com.evenly.took.feature.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Access Token을 재발급 받기 위한 Refresh Token", requiredMode = Schema.RequiredMode.REQUIRED)
public record RefreshTokenRequest(
	String refreshToken
) {
}
