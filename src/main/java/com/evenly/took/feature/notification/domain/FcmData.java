package com.evenly.took.feature.notification.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FcmData {

	private static final Map<String, String> MEMO_DATA = new HashMap<>();
	private static final Map<String, String> INTERESTING_DATA = new HashMap<>();

	private final Map<String, String> value;

	static {
		MEMO_DATA.put("title", "오늘 공유한 명함을 특별하게 만들어 볼까요?");
		MEMO_DATA.put("body", "다음 만남이 훨씬 자연스러워질 거예요");
		MEMO_DATA.put("link", "/card-notes");
		INTERESTING_DATA.put("title", "방금 나와 공통점이 같은 사람을 발견했어요!");
		INTERESTING_DATA.put("body", "어떤 사람인지 살펴보세요");
		INTERESTING_DATA.put("link", "/received/interesting");
	}

	public static FcmData from(NotificationType notificationType) {
		return switch (notificationType) {
			case MEMO -> new FcmData(MEMO_DATA);
			case INTERESTING -> new FcmData(INTERESTING_DATA);
		};
	}

	public Map<String, String> getValue() {
		return Collections.unmodifiableMap(value);
	}
}
