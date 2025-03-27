package com.evenly.took.feature.card.dto.response;

import java.util.List;

public record ReceivedCardListResponse(
	List<CardResponse> cards
) {
}
