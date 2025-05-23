package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "받은 명함 조회 요청")
public record ReceivedCardsRequest(
	@Schema(description = "조회할 명함의 폴더 ID", example = "1")
	Long folderId
) {
}
