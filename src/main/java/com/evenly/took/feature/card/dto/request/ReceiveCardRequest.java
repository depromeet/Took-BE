package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "명함을 수신하기 위한 요청", requiredMode = Schema.RequiredMode.REQUIRED)
public record ReceiveCardRequest(
	@NotNull
	@Schema(description = "수신할 명함 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long cardId
) {
}
