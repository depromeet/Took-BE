package com.evenly.took.feature.notification.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.evenly.took.feature.notification.exception.ExpoException;
import com.evenly.took.feature.notification.exception.ExpoRetryableException;

@Component
public class ExpoMessageSenderErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return false;
	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		ResponseErrorHandler.super.handleError(url, method, response);
	}

	private RuntimeException decideException(Exception ex) {
		if (isRetryable(ex)) {
			return new ExpoRetryableException(ex);
		}
		return new ExpoException(ex);
	}

	private boolean isRetryable(Exception ex) {
		return false;
	}
}
