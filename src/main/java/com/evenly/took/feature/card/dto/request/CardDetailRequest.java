package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "명함 상세 정보 조회 요청")
public record CardDetailRequest(
	@Schema(description = "조회할 명함 ID", example = "1")
	Long id
) {
}
