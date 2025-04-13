package com.evenly.took.feature.notification.client;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.notification.client.dto.request.ExpoSendNotificationRequest;
import com.evenly.took.feature.notification.client.dto.response.ExpoSendNotificationResponse;
import com.evenly.took.feature.notification.domain.UserNotification;
import com.evenly.took.feature.notification.exception.ExpoRetryableException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Retryable(
	retryFor = ExpoRetryableException.class,
	backoff = @Backoff(delay = 10_000, multiplier = 2, random = true)
)
@Component
public class ExpoMessageSender {

	@Value("${expo.url.send-notification}")
	private String url;

	private final RestClient restClient;

	public ExpoMessageSender(RestClient.Builder restClientBuilder, ExpoMessageSenderErrorHandler errorHandler) {
		this.restClient = restClientBuilder.clone()
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE, StandardCharsets.UTF_8.name())
			.build();
	}

	public void send(List<UserNotification> notifications) {
		List<ExpoSendNotificationRequest> request = notifications.stream()
			.map(ExpoSendNotificationRequest::new)
			.toList();
		ResponseEntity<ExpoSendNotificationResponse> response = restClient.post()
			.uri(url)
			.body(request)
			.retrieve()
			.toEntity(ExpoSendNotificationResponse.class);
	}
}
