package com.evenly.took.feature.notification.domain;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.Message;

@Component
public class FcmMessageCreator {

	public Message createMessage(FcmToken token, FcmData data) {
		return Message.builder()
			.setToken(token.getValue())
			.putAllData(data.getValue())
			.build();
	}
}
