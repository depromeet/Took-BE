package com.evenly.took.feature.card.client;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LinkSource {

	NAVER("https://blog.naver.com/", ""),
	BRUNCH("https://brunch.co.kr/", ""),
	BEHANCE("https://www.behance.net/", ""),
	VELOG_MAIN("https://velog.io/", "/posts"),
	BASIC("basic", ""),
	;

	private final String prefix;
	private final String suffix;

	public static LinkSource parseSource(String link) {
		return Arrays.stream(values())
			.filter(value -> link.startsWith(value.prefix) && link.endsWith(value.suffix))
			.findAny()
			.orElse(BASIC);
	}
}
