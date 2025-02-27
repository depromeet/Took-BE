package com.evenly.took.feature.auth.application;

import org.springframework.stereotype.Service;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.auth.dto.response.OAuthUrlResponse;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.security.auth.JwtTokenProvider;
import com.evenly.took.global.security.auth.UuidTokenProvider;
import com.evenly.took.global.security.client.AuthCodeRequestUrlProviderComposite;
import com.evenly.took.global.security.client.UserClientComposite;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final AuthCodeRequestUrlProviderComposite authCodeComposite;
	private final UserClientComposite userClientComposite;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final UuidTokenProvider uuidTokenProvider;

	public OAuthUrlResponse getAuthCodeRequestUrl(OAuthType oauthType) {
		String url = authCodeComposite.provide(oauthType);
		return new OAuthUrlResponse(url);
	}

	public AuthResponse loginAndGenerateToken(OAuthType oauthType, String authCode) {
		User user = userClientComposite.fetch(oauthType, authCode);
		User savedUser = userRepository.findByOauthIdentifier(user.getOauthIdentifier())
			.orElseGet(() -> userRepository.save(user));

	public TokenResponse refreshToken(RefreshTokenRequest request) {
		String refreshToken = request.refreshToken();
		String accessToken = tokenProvider.provideAccessTokenByRefreshToken(refreshToken);
		return new TokenResponse(accessToken, refreshToken);
	}
}
