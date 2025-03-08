package com.evenly.took.feature.card.domain.vo;

import com.evenly.took.feature.card.domain.SNSType;

public record SNS(
	SNSType type,
	String link
) {
}
