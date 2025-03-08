package com.evenly.took.feature.card.dto.response;

import java.util.List;

public record MyCardListResponse(
	List<MyCardResponse> cards
) {
}
