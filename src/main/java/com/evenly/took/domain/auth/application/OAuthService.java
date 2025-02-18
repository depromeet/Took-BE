package com.evenly.took.domain.auth.application;

import org.springframework.stereotype.Service;

import com.evenly.took.domain.auth.client.UserClientComposite;
import com.evenly.took.domain.auth.client.authcode.AuthCodeRequestUrlProviderComposite;
import com.evenly.took.domain.auth.domain.OAuthType;
import com.evenly.took.domain.auth.dto.response.JwtResponse;
import com.evenly.took.domain.auth.jwt.JwtTokenProvider;
import com.evenly.took.domain.user.dao.UserRepository;
import com.evenly.took.domain.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final AuthCodeRequestUrlProviderComposite authCodeComposite;
	private final UserClientComposite userClientComposite;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public String getAuthCodeRequestUrl(OAuthType oauthType) {
		return authCodeComposite.provide(oauthType);
	}

	public JwtResponse loginAndGenerateToken(OAuthType oauthType, String authCode) {
		User user = userClientComposite.fetch(oauthType, authCode);
		User savedUser = userRepository.findByOauthId(user.getOauthId())
			.orElseGet(() -> userRepository.save(user));

		String accessToken = jwtTokenProvider.generateAccessToken(savedUser);

		return new JwtResponse(accessToken);
	}
}
