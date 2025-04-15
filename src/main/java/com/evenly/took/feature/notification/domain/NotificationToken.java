package com.evenly.took.feature.notification.domain;

import com.evenly.took.feature.user.domain.UserDevice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationToken {

	private final String value;

	public static NotificationToken from(UserDevice device) {
		return new NotificationToken(device.getExpoToken());
	}
}
