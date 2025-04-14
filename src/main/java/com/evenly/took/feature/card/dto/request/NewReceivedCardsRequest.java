package com.evenly.took.feature.card.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "새로 추가된 받은 명함 조회 요청")
public record NewReceivedCardsRequest(
	@Schema(description = "기준 시간 (이 시간 이전에 추가된 명함들을 조회) => 입력하지 않는 경우 현재 시점 기준으로 조회", example = "2023-01-01T00:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	LocalDateTime baseTime
) {
}
