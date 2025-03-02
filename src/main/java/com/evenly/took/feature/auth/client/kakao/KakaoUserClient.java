package com.evenly.took.feature.auth.client.kakao;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.client.UserClient;
import com.evenly.took.feature.auth.client.kakao.dto.KakaoTokenResponse;
import com.evenly.took.feature.auth.client.kakao.dto.KakaoUserResponse;
import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
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
	public User fetch(String authCode) {
		KakaoTokenResponse tokenResponse = kakaoTokenProvider.fetchAccessToken(authCode);
		KakaoUserResponse userResponse = kakaoUserInfoProvider.fetchUserInfo(tokenResponse.accessToken());
		return buildUser(userResponse);
	}

	private User buildUser(KakaoUserResponse response) {
		OAuthIdentifier oAuthIdentifier = OAuthIdentifier.builder()
			.oauthId(response.id().toString())
			.oauthType(supportType())
			.build();
		return User.builder()
			.name("dummy_name") // TODO 기획 결정 후 변경
			.oauthIdentifier(oAuthIdentifier)
			.build();
	}
}
