package com.evenly.took.feature.auth.client.google;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.client.UserClient;
import com.evenly.took.feature.auth.client.google.dto.response.GoogleTokenResponse;
import com.evenly.took.feature.auth.client.google.dto.response.GoogleUserInfoResponse;
import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.user.domain.AllowPush;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;

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
	public User fetch(AuthContext authContext) {
		try {
			GoogleTokenResponse tokenResponse = googleTokenProvider.fetchAccessToken(authContext.getAuthCode());
			GoogleUserInfoResponse userInfoResponse = googleUserInfoProvider.fetchUserInfo(tokenResponse.accessToken());

			return generateUser(userInfoResponse);
		} catch (HttpClientErrorException ex) {
			throw new TookException(AuthErrorCode.INVALID_GOOGLE_CONNECTION);
		}
	}

	private User generateUser(GoogleUserInfoResponse userInfoResponse) {
		OAuthIdentifier oauthIdentifier = OAuthIdentifier.builder()
			.oauthId(userInfoResponse.sub())
			.oauthType(OAuthType.GOOGLE)
			.build();

		AllowPush allowPush = AllowPush.builder()
			.allowPushContent(null)
			.allowPushNotification(false)
			.build();

		return User.builder()
			.oauthIdentifier(oauthIdentifier)
			.name(userInfoResponse.name())
			.email(userInfoResponse.email())
			.allowPush(allowPush)
			.build();
	}
}
