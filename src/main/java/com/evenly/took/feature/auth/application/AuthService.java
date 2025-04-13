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
		User savedUser = login(oauthType, authCode);
		TokenDto tokens = tokenProvider.provideTokens(savedUser);
		boolean isFirstLogin = isFirstLogin(request, savedUser);
		return new AuthResponse(tokens, savedUser, isFirstLogin);
	}

	private User login(OAuthType oauthType, String authCode) {
		User user = userClientComposite.fetch(oauthType, authCode);
		return userRepository.findByOauthIdentifier(user.getOauthIdentifier())
			.orElseGet(() -> userRepository.save(user));
	}

	@Transactional
	public AuthResponse loginAndGenerateToken(OAuthType oauthType, AuthContext context, LoginRequest request) {
		User savedUser = login(oauthType, context);
		TokenDto tokens = tokenProvider.provideTokens(savedUser);
		boolean isFirstLogin = isFirstLogin(request, savedUser);
		return new AuthResponse(tokens, savedUser, isFirstLogin);
	}

	private User login(OAuthType oauthType, AuthContext context) {
		User user = userClientComposite.fetch(oauthType, context);
		return userRepository.findByOauthIdentifier(user.getOauthIdentifier())
			.orElseGet(() -> userRepository.save(user));
	}

	private boolean isFirstLogin(LoginRequest request, User user) {
		if (request == null) {
			return false;
		}
		String expoToken = request.expoToken();
		boolean existDevice = userDeviceRepository.existsByUserAndExpoToken(user, expoToken);
		if (existDevice) {
			return false;
		}
		UserDevice userDevice = new UserDevice(user, expoToken);
		userDeviceRepository.save(userDevice);
		return true;
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
