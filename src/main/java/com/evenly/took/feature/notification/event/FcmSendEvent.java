package com.evenly.took.feature.notification.event;

import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.evenly.took.feature.notification.domain.FcmNotification;

public class FcmSendEvent extends ApplicationEvent {

	private final List<FcmNotification> fcmNotifications;

	public FcmSendEvent(Object source, List<FcmNotification> fcmNotifications) {
		super(source);
		this.fcmNotifications = fcmNotifications;
	}

	public List<FcmNotification> getFcmNotifications() {
		return Collections.unmodifiableList(fcmNotifications);
	}
}
