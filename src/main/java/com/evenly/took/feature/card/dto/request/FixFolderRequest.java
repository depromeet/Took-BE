package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "폴더를 수정하기 위한 정보", requiredMode = Schema.RequiredMode.REQUIRED)
public record FixFolderRequest(

	@NotNull()
	@Schema(description = "폴더 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long folderId,

	@NotBlank()
	@Schema(description = "폴더명", example = "디프만", requiredMode = Schema.RequiredMode.REQUIRED)
	String name
) {
}
