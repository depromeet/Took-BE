package com.evenly.took.feature.card.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "받은 명함을 폴더에 저장하기 위한 요청", requiredMode = Schema.RequiredMode.REQUIRED)
public record SetReceivedCardsFolderRequest(
	@NotNull
	@Schema(description = "폴더 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long folderId,

	@NotEmpty
	@Schema(description = "받은 명함 ID 목록", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
	List<Long> receivedCardIds
) {
}
