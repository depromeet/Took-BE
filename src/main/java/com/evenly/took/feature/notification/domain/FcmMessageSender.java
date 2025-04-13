package com.evenly.took.feature.notification.domain;

import java.util.List;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.notification.exception.FcmException;
import com.evenly.took.feature.notification.exception.FcmRetryableException;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.SendResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Retryable(
	retryFor = FcmRetryableException.class,
	backoff = @Backoff(delay = 10_000, multiplier = 2, random = true)
)
@Component
public class FcmMessageSender {

	private static final int FCM_SERVER_ERROR_CODE = 500;

	public void send(List<Message> messages) { // TODO 보완
		try {
			BatchResponse batchResponse = FirebaseMessaging.getInstance().sendEach(messages);
			log.info("전송 성공 {}개, 전송 실패 {}개", batchResponse.getSuccessCount(), batchResponse.getFailureCount());
			List<SendResponse> responses = batchResponse.getResponses();
			responses.stream()
				.filter(response -> !response.isSuccessful())
				.forEach(response -> log.error("알림 전송 시 실패 원인: ", response.getException()));
		} catch (Exception ex) {
			throw decideException(ex);
		}
	}

	private RuntimeException decideException(Exception ex) {
		if (isRetryable(ex)) {
			return new FcmRetryableException(ex);
		}
		return new FcmException(ex);
	}

	private boolean isRetryable(Exception ex) {
		return ex instanceof FirebaseMessagingException firebaseEx
			&& firebaseEx.getHttpResponse().getStatusCode() >= FCM_SERVER_ERROR_CODE;
	}
}
