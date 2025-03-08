package com.evenly.took.feature.auth.client.apple;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.client.UserClient;
import com.evenly.took.feature.auth.client.apple.dto.response.AppleTokenResponse;
import com.evenly.took.feature.auth.client.apple.dto.response.AppleUserResponse;
import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleUserClient implements UserClient {

	private final AppleTokenProvider appleTokenProvider;
	private final AppleUserInfoProvider appleUserInfoProvider;

	@Override
	public OAuthType supportType() {
		return OAuthType.APPLE;
	}

	@Override
	public User fetch(AuthContext authContext) {
		try {
			// 1. 인증 코드로 ID 토큰 요청
			AppleTokenResponse tokenResponse = appleTokenProvider.fetchIdToken(authContext.getAuthCode());

			AppleUserResponse userResponse;
			// 2. ID 토큰에서 사용자 정보 추출
			if (authContext.hasName()) {
				// 회원가입
				userResponse = appleUserInfoProvider.fetchSignupUser(tokenResponse.idToken(),
					authContext.getName());
			} else {
				// 로그인
				userResponse = appleUserInfoProvider.fetchLoginUser(tokenResponse.idToken());
			}

			// 3. 사용자 정보로 User 객체 생성
			return buildUser(userResponse);
		} catch (TookException e) {
			throw e;
		} catch (Exception e) {
			log.error("Failed to fetch Apple user: {}", e.getMessage(), e);
			throw new TookException(AuthErrorCode.APPLE_SERVER_ERROR);
		}
	}

	private User buildUser(AppleUserResponse response) {
		OAuthIdentifier oAuthIdentifier = OAuthIdentifier.builder()
			.oauthId(response.id())
			.oauthType(supportType())
			.build();

		return User.builder()
			.name(response.name() != null ? response.name() : response.email())
			.email(response.email())
			.oauthIdentifier(oAuthIdentifier)
			.build();
	}
}
