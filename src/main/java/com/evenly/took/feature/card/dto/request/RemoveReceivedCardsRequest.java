package com.evenly.took.feature.card.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "받은 명함 삭제를 위한 요청", requiredMode = Schema.RequiredMode.REQUIRED)
public record RemoveReceivedCardsRequest(
	@NotEmpty
	@Schema(description = "삭제할 명함 ID 목록", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
	List<Long> cardIds
) {
}
