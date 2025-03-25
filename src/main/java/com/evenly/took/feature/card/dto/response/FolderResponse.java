package com.evenly.took.feature.card.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "명함 폴더 응답")
public record FolderResponse(
	@Schema(description = "폴더 ID", example = "1")
	Long id,

	@Schema(description = "폴더명", example = "디프만")
	String name
) {
}
