package com.evenly.took.feature.notification.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.notification.domain.FcmNotification;
import com.evenly.took.feature.notification.event.FcmSendEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class FcmEventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	public void publishFcmSendEvent(List<FcmNotification> fcmNotifications) {
		eventPublisher.publishEvent(new FcmSendEvent(this, fcmNotifications));
	}
}
