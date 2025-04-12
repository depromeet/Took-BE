package com.evenly.took.feature.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.auth.client.AuthCodeRequestUrlProviderComposite;
import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.TokenDto;
import com.evenly.took.feature.auth.dto.request.LoginRequest;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.request.WithdrawRequest;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.auth.dto.response.OAuthUrlResponse;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.feature.user.dao.UserDeviceRepository;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.domain.UserDevice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthCodeRequestUrlProviderComposite authCodeComposite;
	private final UserClientComposite userClientComposite;
	private final UserRepository userRepository;
	private final UserDeviceRepository userDeviceRepository;
	private final TokenProvider tokenProvider;
	private final WithdrawService withdrawService;

	public OAuthUrlResponse getAuthCodeRequestUrl(OAuthType oauthType) {
		String url = authCodeComposite.provide(oauthType);
		return new OAuthUrlResponse(url);
	}

	@Transactional
	public AuthResponse loginAndGenerateToken(OAuthType oauthType, String authCode, LoginRequest request) {
		User user = userClientComposite.fetch(oauthType, authCode);
		User savedUser = userRepository.findByOauthIdentifier(user.getOauthIdentifier())
			.orElseGet(() -> userRepository.save(user));
		TokenDto tokens = tokenProvider.provideTokens(savedUser);
		if (request == null) {
			return new AuthResponse(tokens, savedUser, false);
		}
		String fcmToken = request.fcmToken();
		boolean isFirstLogin = !userDeviceRepository.existsByUserAndFcmToken(savedUser, fcmToken);
		if (isFirstLogin) {
			UserDevice userDevice = new UserDevice(savedUser, fcmToken);
			userDeviceRepository.save(userDevice);
		}
		return new AuthResponse(tokens, savedUser, isFirstLogin);
	}

	@Transactional
	public AuthResponse loginAndGenerateToken(OAuthType oauthType, AuthContext context, LoginRequest request) {
		User user = userClientComposite.fetch(oauthType, context);
		User savedUser = userRepository.findByOauthIdentifier(user.getOauthIdentifier())
			.orElseGet(() -> userRepository.save(user));
		TokenDto tokens = tokenProvider.provideTokens(savedUser);
		if (request == null) {
			return new AuthResponse(tokens, savedUser, false);
		}
		String fcmToken = request.fcmToken();
		boolean isFirstLogin = !userDeviceRepository.existsByUserAndFcmToken(savedUser, fcmToken);
		if (isFirstLogin) {
			UserDevice userDevice = new UserDevice(savedUser, fcmToken);
			userDeviceRepository.save(userDevice);
		}
		return new AuthResponse(tokens, savedUser, isFirstLogin);
	}

	public TokenResponse refreshToken(RefreshTokenRequest request) {
		String refreshToken = request.refreshToken();
		String accessToken = tokenProvider.provideAccessTokenByRefreshToken(refreshToken);
		return new TokenResponse(accessToken, refreshToken);
	}

	public void logout(String refreshToken) {
		tokenProvider.invalidateRefreshToken(refreshToken);
	}

	public void withdraw(Long userId, WithdrawRequest request) {
		withdrawService.withdraw(userId, request);
	}
}
