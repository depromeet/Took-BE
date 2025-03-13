package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "명함 상세 정보 조회 요청")
public record CardDetailRequest(
	@NotNull
	@Schema(description = "조회할 명함 ID", example = "1")
	Long cardId
) {
}
