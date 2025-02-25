package com.evenly.took.feature.auth.application;

import org.springframework.stereotype.Service;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.security.client.AuthCodeRequestUrlProviderComposite;
import com.evenly.took.global.security.client.UserClientComposite;
import com.evenly.took.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final AuthCodeRequestUrlProviderComposite authCodeComposite;
	private final UserClientComposite userClientComposite;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final UuidTokenProvider uuidTokenProvider;

	public String getAuthCodeRequestUrl(OAuthType oauthType) {
		return authCodeComposite.provide(oauthType);
	}

	public AuthResponse loginAndGenerateToken(OAuthType oauthType, String authCode) {
		User user = userClientComposite.fetch(oauthType, authCode);
		User savedUser = userRepository.findByOauthIdentifier(user.getOauthIdentifier())
			.orElseGet(() -> userRepository.save(user));

		String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getId().toString());
		String refreshToken = uuidTokenProvider.generateRefreshToken(savedUser.getId().toString());

		return new AuthResponse(accessToken, refreshToken, user);
	}
}
