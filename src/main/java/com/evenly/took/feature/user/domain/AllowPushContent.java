package com.evenly.took.feature.user.domain;

import java.util.Arrays;

import com.evenly.took.feature.user.exception.UserErrorCode;
import com.evenly.took.global.exception.TookException;

public enum AllowPushContent {

	MEMO,
	INTERESTING,
	SYSTEM,
	;

	public static AllowPushContent asContent(String content) {
		return Arrays.stream(values())
			.filter(value -> value.name().equals(content))
			.findAny()
			.orElseThrow(() -> new TookException(UserErrorCode.INVALID_ALLOW_PUSH_CONTENT));
	}
}
