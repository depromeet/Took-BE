package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "링크 스크랩 요청")
public record LinkRequest(
	@Schema(description = "링크 플랫폼")
	@NotBlank(message = "유효하지 않은 플랫폼입니다.") String source,

	@Schema(description = "링크")
	@NotBlank(message = "유효하지 않은 링크입니다.") String link
) {
}
