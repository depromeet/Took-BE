package com.evenly.took.feature.notification.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.notification.exception.FcmRetryableException;
import com.evenly.took.feature.notification.exception.NotificationErrorCode;
import com.evenly.took.global.exception.TookException;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Retryable(
// 	retryFor = NotificationRetryableException.class,
// 	backoff = @Backoff(delay = 10_000, multiplier = 2, random = true)
// )
@Component
public class FcmMessageSender {

	private static final int FCM_SERVER_ERROR_CODE = 500;

	public void send(List<Message> messages) { // TODO
		try {
			ApiFuture<BatchResponse> future = FirebaseMessaging.getInstance().sendEachAsync(messages);
			// log.info("전송 성공 {}개, 전송 실패 {}개", response.getSuccessCount(), response.getFailureCount());
			// return response;
		} catch (Exception ex) {
			throw decideException(ex);
		}
	}

	private RuntimeException decideException(Exception ex) {
		if (isRetryable(ex)) {
			return new FcmRetryableException(ex);
		}
		return new TookException(NotificationErrorCode.FCM_4XX_ERROR);
	}

	private boolean isRetryable(Exception ex) {
		return ex instanceof FirebaseMessagingException firebaseEx
			&& firebaseEx.getHttpResponse().getStatusCode() >= FCM_SERVER_ERROR_CODE;
	}
}
