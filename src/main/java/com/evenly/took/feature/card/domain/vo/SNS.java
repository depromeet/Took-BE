package com.evenly.took.feature.card.domain.vo;

import com.evenly.took.feature.card.domain.SNSType;
import com.evenly.took.feature.card.dto.response.SNSResponse;

public record SNS(
	SNSType type,
	String link
) {
	public SNSResponse toDto() {
		return new SNSResponse(type, link);
	}
}
