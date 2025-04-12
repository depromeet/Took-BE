package com.evenly.took.feature.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FcmToken {

	private final String value;

	public static FcmToken from(UserDevice device) {
		return new FcmToken(device.getFcmToken());
	}
}
