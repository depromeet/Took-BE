package com.evenly.took.feature.card.client;

import java.util.Arrays;

public enum CrawledType {

	VELOG,
	BEHANCE, // TODO 추가해야함
	BRUNCH,
	BASIC, // GITHUB, PLAY_STORE, APP_STORE, TISTORY
	;

	public static CrawledType asType(String link) {
		return Arrays.stream(values())
			.filter(value -> link.contains(value.name().toLowerCase()))
			.findAny()
			.orElse(BASIC);
	}
}
