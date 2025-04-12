package com.evenly.took.feature.notification.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.evenly.took.feature.notification.domain.FcmData;
import com.evenly.took.feature.notification.domain.FcmMessageCreator;
import com.evenly.took.feature.notification.domain.FcmMessageSender;
import com.evenly.took.feature.notification.domain.FcmNotification;
import com.evenly.took.feature.notification.domain.FcmToken;
import com.evenly.took.feature.notification.domain.NotificationType;
import com.evenly.took.feature.user.dao.UserDeviceRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.domain.UserDevice;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FcmService {

	private final UserDeviceRepository userDeviceRepository;
	private final FcmMessageCreator messageCreator;
	private final FcmMessageSender messageSender;

	public void sendFcm(List<FcmNotification> fcmNotifications) {
		List<Message> messages = new ArrayList<>();
		for (FcmNotification fcmNotification : fcmNotifications) {
			User user = fcmNotification.getUser();
			NotificationType type = fcmNotification.getNotificationType();
			FcmData fcmData = FcmData.from(type);
			List<UserDevice> userDevices = userDeviceRepository.findByUser(user);
			userDevices.stream()
				.filter(UserDevice::isAllowPushNotification) // TODO 기능별 allow
				.map(FcmToken::from)
				.map(fcmToken -> messageCreator.createMessage(fcmToken, fcmData))
				.forEach(messages::add);
		}
		messageSender.send(messages); // TODO 비동기 결과 처리
	}
}
