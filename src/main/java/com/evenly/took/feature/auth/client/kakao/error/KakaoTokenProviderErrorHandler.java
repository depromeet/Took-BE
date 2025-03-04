package com.evenly.took.feature.auth.client.kakao.error;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.evenly.took.feature.auth.client.kakao.dto.response.KakaoErrorResponse;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KakaoTokenProviderErrorHandler implements ResponseErrorHandler {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final String ERROR_CODE_WHEN_INVALID_CODE = "KOE320";

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().isError();
	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		byte[] responseBody = response.getBody().readAllBytes();
		if (response.getStatusCode().is4xxClientError()) {
			handle4xxError(response, new String(responseBody));
		}
		log.error("카카오 소셜 로그인 과정에서 에러 발생: {}", new String(responseBody));
		throw new TookException(AuthErrorCode.KAKAO_SERVER_ERROR);
	}

	private void handle4xxError(ClientHttpResponse response, String responseBody) throws IOException {
		if (response.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
			throw new TookException(AuthErrorCode.KAKAO_INVALID_APP_INFO);
		}
		if (responseBody == null || responseBody.isBlank()) {
			throw new TookException(AuthErrorCode.KAKAO_SERVER_ERROR);
		}
		if (hasErrorCodeOf(responseBody, ERROR_CODE_WHEN_INVALID_CODE)) {
			throw new TookException(AuthErrorCode.KAKAO_INVALID_AUTH_CODE);
		}
	}

	private boolean hasErrorCodeOf(String responseBody, String errorCode) {
		try {
			KakaoErrorResponse response = OBJECT_MAPPER.readValue(responseBody, KakaoErrorResponse.class);
			return response.errorCode().equals(errorCode);
		} catch (Exception ex) {
			throw new TookException(AuthErrorCode.KAKAO_SERVER_ERROR);
		}
	}
}
