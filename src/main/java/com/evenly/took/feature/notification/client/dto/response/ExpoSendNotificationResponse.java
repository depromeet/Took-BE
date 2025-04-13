package com.evenly.took.feature.notification.client.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;

public record ExpoSendNotificationResponse(

	@JsonAlias("data.status")
	String status,

	@JsonAlias("data.id")
	String id
) {
}
