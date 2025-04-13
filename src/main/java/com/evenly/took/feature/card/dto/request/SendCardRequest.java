package com.evenly.took.feature.card.dto.request;

import jakarta.validation.constraints.NotNull;

public record SendCardRequest(
	@NotNull Long targetUserId,
	@NotNull Long cardId
) {
}
