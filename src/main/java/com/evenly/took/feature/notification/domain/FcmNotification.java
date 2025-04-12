package com.evenly.took.feature.notification.domain;

import com.evenly.took.feature.user.domain.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FcmNotification {

	private final User user;
	private final NotificationType notificationType;
}
