package com.evenly.took.feature.card.client;

import java.util.Arrays;

public enum LinkSource {

	VELOG,
	NAVER, // NAVER_BLOG
	BRUNCH,
	BEHANCE, // TODO 추가해야함
	BASIC, // GITHUB, PLAY_STORE, APPLE_STORE, TISTORY
	;

	public static LinkSource asLinkSource(String source) {
		return Arrays.stream(values())
			.filter(value -> source.equals(value.name().toLowerCase())) // TODO
			.findAny()
			.orElse(BASIC);
	}
}
