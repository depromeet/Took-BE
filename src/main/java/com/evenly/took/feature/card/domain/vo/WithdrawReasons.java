package com.evenly.took.feature.card.domain.vo;

import java.util.List;

public record WithdrawReasons(
	List<String> reasons,
	String directMessage
) {
}
