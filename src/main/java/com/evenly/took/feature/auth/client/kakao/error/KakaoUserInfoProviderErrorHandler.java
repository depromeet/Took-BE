package com.evenly.took.feature.auth.client.kakao.error;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KakaoUserInfoProviderErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().isError();
	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().is4xxClientError()) {
			handle4xxError(response);
		}
		log.error("카카오 소셜 로그인 과정에서 에러 발생: {}", new String(response.getBody().readAllBytes()));
		throw new TookException(AuthErrorCode.KAKAO_SERVER_ERROR);
	}

	private void handle4xxError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
			throw new TookException(AuthErrorCode.KAKAO_INVALID_ACCESS_TOKEN);
		}
	}
}
