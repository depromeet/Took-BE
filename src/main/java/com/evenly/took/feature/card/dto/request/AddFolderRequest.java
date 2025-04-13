package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "폴더를 추가하기 위한 정보", requiredMode = Schema.RequiredMode.REQUIRED)
public record AddFolderRequest(
	@NotBlank()
	@Schema(description = "폴더명", example = "디프만", requiredMode = Schema.RequiredMode.REQUIRED)
	String name
) {
}
