package com.evenly.took.feature.notification.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.notification.client.dto.request.ExpoPushReceiptsRequest;
import com.evenly.took.feature.notification.client.dto.request.ExpoPushTicketsRequest;
import com.evenly.took.feature.notification.client.dto.response.ExpoPushReceiptsResponse;
import com.evenly.took.feature.notification.client.dto.response.ExpoPushTicketsResponse;
import com.evenly.took.feature.notification.domain.UserNotification;
import com.evenly.took.feature.notification.exception.ExpoRetryableException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Retryable(
	maxAttempts = 3,
	retryFor = ExpoRetryableException.class,
	backoff = @Backoff(delay = 5_000, multiplier = 2, random = true)
)
@Component
public class ExpoNotificationSender {

	@Value("${expo.url.send-notification}")
	private String pushTicketsUrl;

	@Value("${expo.url.get-receipts}")
	private String pushReceiptsUrl;

	private final RestClient restClient;

	public ExpoNotificationSender(RestClient.Builder restClientBuilder, ExpoMessageSenderErrorHandler errorHandler) {
		this.restClient = restClientBuilder.clone()
			.defaultStatusHandler(errorHandler)
			.build();
	}

	public List<String> pushTickets(List<UserNotification> notifications) {
		List<ExpoPushTicketsRequest> request = notifications.stream()
			.map(ExpoPushTicketsRequest::new)
			.toList();
		ResponseEntity<ExpoPushTicketsResponse> response = restClient.post()
			.uri(pushTicketsUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.retrieve()
			.toEntity(ExpoPushTicketsResponse.class);
		ExpoPushTicketsResponse responseBody = response.getBody();
		return responseBody.getSuccessTicketIds();
	}

	public List<String> pushReceipts(List<String> ids) {
		ExpoPushReceiptsRequest request = new ExpoPushReceiptsRequest(ids);
		ResponseEntity<ExpoPushReceiptsResponse> response = restClient.post()
			.uri(pushReceiptsUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.retrieve()
			.toEntity(ExpoPushReceiptsResponse.class);
		ExpoPushReceiptsResponse responseBody = response.getBody();
		return responseBody.getFailIds(ids);
	}
}
