package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "링크 스크랩 정보 조회 요청")
public record LinkRequest(
	@Schema(description = "링크")
	String link
) {
}
