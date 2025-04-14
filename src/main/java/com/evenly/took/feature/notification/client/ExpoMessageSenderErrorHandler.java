package com.evenly.took.feature.notification.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.evenly.took.feature.notification.exception.ExpoRetryableException;
import com.evenly.took.feature.notification.exception.NotificationErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExpoMessageSenderErrorHandler implements ResponseErrorHandler {

	private static final int STATUS_CODE_OF_TOO_MANY_REQUESTS = 429;

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().isError();
	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		byte[] responseBody = response.getBody().readAllBytes();
		if (isRetryable(response)) {
			log.warn("Expo 알림 전송 과정에서 429 혹은 5xx 에러 발생하여 재시도: {}", new String(responseBody));
			throw new ExpoRetryableException(new String(responseBody));
		}
		log.error("Expo 알림 전송 과정에서 에러 발생: {}", new String(responseBody));
		throw new TookException(NotificationErrorCode.EXPO_SERVER_ERROR);
	}

	private boolean isRetryable(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().is5xxServerError()
			|| response.getStatusCode().value() == STATUS_CODE_OF_TOO_MANY_REQUESTS;
	}
}
