package com.evenly.took.feature.auth.client.kakao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.evenly.took.feature.auth.client.kakao.dto.KakaoErrorResponse;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KakaoResponseErrorHandler implements ResponseErrorHandler {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().is4xxClientError()) {
			handle4xxError(response);
		}
		if (response.getStatusCode().is5xxServerError()) {
			handle5xxError(response);
		}
	}

	private void handle4xxError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
			throw new TookException(AuthErrorCode.KAKAO_INVALID_APP_INFO);
		}
		if (hasErrorCodeOf(response.getBody(), "KOE320")) {
			throw new TookException(AuthErrorCode.KAKAO_INVALID_AUTH_CODE);
		}
	}

	private boolean hasErrorCodeOf(InputStream responseBody, String errorCode) throws IOException {
		KakaoErrorResponse response = OBJECT_MAPPER.readValue(responseBody, KakaoErrorResponse.class);
		return response.errorCode().equals(errorCode);
	}

	private void handle5xxError(ClientHttpResponse response) throws IOException {
		log.error("카카오 소셜 로그인 과정에서 에러 발생: {}", new String(response.getBody().readAllBytes()));
		throw new TookException(AuthErrorCode.KAKAO_SERVER_ERROR);
	}
}
