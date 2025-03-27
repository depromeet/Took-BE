package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "수신된 명함을 수정하기 위한 정보", requiredMode = Schema.RequiredMode.REQUIRED)
public record FixReceivedCardRequest(

	@NotNull
	@Schema(description = "명함 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long cardId,

	@Schema(description = "메모 내용", example = "디프만에서 만난 개발자")
	String memo
) {
}
