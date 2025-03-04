package com.evenly.took.feature.auth.client.google;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GoogleUserInfoProviderErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().isError();
	}

	@Override
	public void handleError(URI uri, HttpMethod method, ClientHttpResponse response) throws IOException {
		String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);

		if (response.getStatusCode().is4xxClientError()) {
			throw new TookException(AuthErrorCode.INVALID_GOOGLE_USER_REQUEST);
		}

		if (response.getStatusCode().is5xxServerError()) {
			throw new TookException(AuthErrorCode.INVALID_GOOGLE_SERVER_ERROR);
		}

		log.error("Google 사용자정보 알 수 없는 오류: {}", responseBody);
		throw new TookException(AuthErrorCode.INVALID_GOOGLE_CONNECTION);
	}
}
