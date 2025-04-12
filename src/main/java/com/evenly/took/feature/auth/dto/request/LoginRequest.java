package com.evenly.took.feature.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "푸시알림 전송을 위한 Fcm Token")
public record LoginRequest(
	String fcmToken
) {
}
