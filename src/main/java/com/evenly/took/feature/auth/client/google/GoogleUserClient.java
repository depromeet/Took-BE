package com.evenly.took.feature.auth.client.google;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.evenly.took.feature.auth.client.UserClient;
import com.evenly.took.feature.auth.client.google.dto.GoogleTokenResponse;
import com.evenly.took.feature.auth.client.google.dto.GoogleUserInfoResponse;
import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.feature.user.domain.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleUserClient implements UserClient {

	private final GoogleTokenProvider googleTokenProvider;
	private final GoogleUserInfoProvider googleUserInfoProvider;

	@Override
	public OAuthType supportType() {
		return OAuthType.GOOGLE;
	}

	@Override
	public User fetch(String authCode) {
		try {
			GoogleTokenResponse tokenResponse = googleTokenProvider.fetchAccessToken(authCode);
			GoogleUserInfoResponse userInfoResponse = googleUserInfoProvider.fetchUserInfo(tokenResponse.accessToken());

			return generateUser(userInfoResponse.sub(), userInfoResponse.name());
		} catch (HttpClientErrorException ex) {
			throw new TookException(AuthErrorCode.INVALID_GOOGLE_CONNECTION);
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
