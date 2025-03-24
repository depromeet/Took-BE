package com.evenly.took.feature.auth.application;

import org.springframework.stereotype.Service;

import com.evenly.took.feature.auth.client.AuthCodeRequestUrlProviderComposite;
import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.TokenDto;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.auth.dto.response.OAuthUrlResponse;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthCodeRequestUrlProviderComposite authCodeComposite;
	private final UserClientComposite userClientComposite;
	private final UserRepository userRepository;
	private final TokenProvider tokenProvider;

	public OAuthUrlResponse getAuthCodeRequestUrl(OAuthType oauthType) {
		String url = authCodeComposite.provide(oauthType);
		return new OAuthUrlResponse(url);
	}

	public AuthResponse loginAndGenerateToken(OAuthType oauthType, String authCode) {
		User user = userClientComposite.fetch(oauthType, authCode);
		User savedUser = userRepository.findByOauthIdentifier(user.getOauthIdentifier())
			.orElseGet(() -> userRepository.save(user));
		TokenDto tokens = tokenProvider.provideTokens(savedUser);
		return new AuthResponse(tokens, savedUser);
	}

	public AuthResponse loginAndGenerateToken(OAuthType oauthType, AuthContext context) {
		User user = userClientComposite.fetch(oauthType, context);
		User savedUser = userRepository.findByOauthIdentifier(user.getOauthIdentifier())
			.orElseGet(() -> userRepository.save(user));
		// TODO UserDevice (token, user) 필드로 조회한 게 없으면 추가하거나 주인 변경 (멀티디바이스 & 한 기기 당 여러 사용자 상황 고려)
		TokenDto tokens = tokenProvider.provideTokens(savedUser);
		return new AuthResponse(tokens, savedUser);
	}

	public TokenResponse refreshToken(RefreshTokenRequest request) {
		String refreshToken = request.refreshToken();
		String accessToken = tokenProvider.provideAccessTokenByRefreshToken(refreshToken);
		return new TokenResponse(accessToken, refreshToken);
	}

	public void logout(String refreshToken) {
		tokenProvider.invalidateRefreshToken(refreshToken);
	}
}
