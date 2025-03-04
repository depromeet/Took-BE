package com.evenly.took.feature.auth.client.apple.error;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.evenly.took.feature.auth.client.apple.dto.response.AppleErrorResponse;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AppleErrorHandler implements ResponseErrorHandler {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().isError();
	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().is4xxClientError()) {
			try {
				handle4xxError(response);
			} catch (TookException e) {
				throw e;
			}
			// 나머지 4xx 상태는 여기서 공통 예외 처리
			log.error("처리되지 않은 Apple 4xx 에러 발생: {}", new String(response.getBody().readAllBytes()));
			throw new TookException(AuthErrorCode.APPLE_SERVER_ERROR);
		}
		log.error("애플 소셜 로그인 과정에서 에러 발생: {}", new String(response.getBody().readAllBytes()));
		throw new TookException(AuthErrorCode.APPLE_SERVER_ERROR);
	}

	/**
	 * 4xx 에러를 처리하는 추상 메서드
	 * 각 하위 클래스에서 구체적인 에러 처리 로직 구현
	 */
	protected abstract void handle4xxError(ClientHttpResponse response) throws IOException;

	/**
	 * 에러 응답을 AppleErrorResponse 객체로 파싱
	 */
	protected AppleErrorResponse parseErrorResponse(InputStream responseBody) throws IOException {
		try {
			return OBJECT_MAPPER.readValue(responseBody, AppleErrorResponse.class);
		} catch (Exception e) {
			log.error("애플 에러 응답 파싱 실패", e);
			throw new TookException(AuthErrorCode.APPLE_SERVER_ERROR);
		}
	}

	/**
	 * 특정 에러 코드인지 확인
	 */
	protected boolean hasErrorCode(AppleErrorResponse response, String errorCode) {
		return errorCode.equals(response.error());
	}

	/**
	 * 401 Unauthorized 상태 코드인지 확인
	 */
	protected boolean isUnauthorized(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED);
	}
}
