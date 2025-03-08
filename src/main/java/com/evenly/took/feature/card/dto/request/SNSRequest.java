package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "SNS 정보")
public record SNSRequest(
	@Schema(description = "SNS 타입", example = "LINKEDIN")
	String type,

	@Schema(description = "SNS 링크", example = "https://linkedin.com/in/username")
	String link
) {
}
