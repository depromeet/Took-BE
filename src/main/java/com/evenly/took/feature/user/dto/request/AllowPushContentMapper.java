package com.evenly.took.feature.user.dto.request;

import java.util.Arrays;
import java.util.List;

import com.evenly.took.feature.user.domain.AllowPushContent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AllowPushContentMapper {

	INTERESTING_CARD_RESPONSE(AllowPushContent.INTERESTING_CARD, "흥미로운 명함 알림"),
	CARD_MEMO_RESPONSE(AllowPushContent.CARD_MEMO, "한 줄 메모 알림"),
	SYSTEM_UPDATE_RESPONSE(AllowPushContent.SYSTEM_UPDATE, "서비스 업데이트 알림"),
	;

	private final AllowPushContent allowPushContent;
	private final String response;

	public static List<AllowPushContent> asAllowPushContents(List<String> responses) {
		return Arrays.stream(values())
			.filter(value -> responses.contains(value.response))
			.map(value -> value.allowPushContent)
			.toList();
	}

	public static List<String> asResponses(List<AllowPushContent> allowPushContents) {
		return Arrays.stream(values())
			.filter(value -> allowPushContents.contains(value.allowPushContent))
			.map(value -> value.response)
			.toList();
	}
}
