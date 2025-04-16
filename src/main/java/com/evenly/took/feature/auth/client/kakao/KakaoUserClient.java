package com.evenly.took.feature.auth.client.kakao;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.client.UserClient;
import com.evenly.took.feature.auth.client.kakao.dto.response.KakaoTokenResponse;
import com.evenly.took.feature.auth.client.kakao.dto.response.KakaoUserResponse;
import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.AllowPush;
import com.evenly.took.feature.user.domain.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoUserClient implements UserClient {

	private final KakaoTokenProvider kakaoTokenProvider;
	private final KakaoUserInfoProvider kakaoUserInfoProvider;

	@Override
	public OAuthType supportType() {
		return OAuthType.KAKAO;
	}

	@Override
	public User fetch(AuthContext authContext) {
		KakaoTokenResponse tokenResponse = kakaoTokenProvider.fetchAccessToken(authContext.getAuthCode());
		KakaoUserResponse userResponse = kakaoUserInfoProvider.fetchUserInfo(tokenResponse.accessToken());
		return buildUser(userResponse);
	}

	private User buildUser(KakaoUserResponse response) {
		OAuthIdentifier oAuthIdentifier = OAuthIdentifier.builder()
			.oauthId(response.id().toString())
			.oauthType(supportType())
			.build();

		AllowPush allowPush = AllowPush.builder()
			.allowPushContent(null)
			.allowPushNotification(false)
			.build();

		return User.builder()
			.name(response.kakaoAccount().nickname())
			.email(response.kakaoAccount().email())
			.oauthIdentifier(oAuthIdentifier)
			.allowPush(allowPush)
			.build();
	}
}
