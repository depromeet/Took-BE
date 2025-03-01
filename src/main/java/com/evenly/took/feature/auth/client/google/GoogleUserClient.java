package com.evenly.took.feature.auth.client.google;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.UserClient;
import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.config.properties.auth.GoogleProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleUserClient implements UserClient {

	private final GoogleProperties googleProperties;
	private final RestClient.Builder restClientBuilder;

	@Override
	public OAuthType supportType() {
		return OAuthType.GOOGLE;
	}

	@Override
	public User fetch(String authCode) {
		try {
			RestClient restClient = restClientBuilder.build();

			MultiValueMap<String, String> tokenRequestParams = getTokenRequestParams(authCode);
			Map tokenResponse = requestAccessToken(restClient, tokenRequestParams);

			String accessToken = tokenResponse.get("access_token").toString();
			Map userInfoResponse = requestUserInfo(restClient, accessToken);

			String oauthId = userInfoResponse.get("sub").toString();
			String name = userInfoResponse.get("name").toString();
			return generateUser(oauthId, name);

		} catch (HttpClientErrorException ex) {
			throw new TookException(AuthErrorCode.INVALID_GOOGLE_CONNECTION);
		}
	}

	private MultiValueMap<String, String> getTokenRequestParams(String authCode) {
		MultiValueMap<String, String> tokenRequestParams = new LinkedMultiValueMap<>();
		tokenRequestParams.add("code", authCode);
		tokenRequestParams.add("client_id", googleProperties.clientId());
		tokenRequestParams.add("client_secret", googleProperties.clientSecret());
		tokenRequestParams.add("redirect_uri", googleProperties.redirectUri());
		tokenRequestParams.add("grant_type", "authorization_code");
		return tokenRequestParams;
	}

	private Map requestAccessToken(RestClient restClient, MultiValueMap<String, String> tokenRequestParams) {
		Map tokenResponse = restClient.post()
			.uri(googleProperties.tokenUri())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.body(tokenRequestParams)
			.retrieve()
			.body(Map.class);

		validateClientResponse(tokenResponse, "access_token", AuthErrorCode.INVALID_GOOGLE_TOKEN);
		return tokenResponse;
	}

	private Map requestUserInfo(final RestClient restClient, final String accessToken) {
		Map userInfoResponse = restClient.get()
			.uri(googleProperties.userInfoUrl())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.body(Map.class);

		validateClientResponse(userInfoResponse, "sub", AuthErrorCode.INVALID_GOOGLE_USER_NOT_FOUND);
		return userInfoResponse;
	}

	private void validateClientResponse(Map response, String key, AuthErrorCode errorCode) {
		if (response == null || !response.containsKey(key)) {
			throw new TookException(errorCode);
		}
	}

	private User generateUser(String oauthId, String name) {
		OAuthIdentifier oauthIdentifier = OAuthIdentifier.builder()
			.oauthId(oauthId)
			.oauthType(OAuthType.GOOGLE)
			.build();

		return User.builder()
			.oauthIdentifier(oauthIdentifier)
			.name(name)
			.build();
	}
}
