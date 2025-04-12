package com.evenly.took.feature.auth.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.auth.application.AuthService;
import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.request.LoginRequest;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.request.WithdrawRequest;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.auth.dto.response.OAuthUrlResponse;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.meta.LoginUser;
import com.evenly.took.global.auth.meta.PublicApi;
import com.evenly.took.global.auth.meta.SecuredApi;
import com.evenly.took.global.logging.meta.PrivacySensitiveLogging;
import com.evenly.took.global.response.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@PublicApi
@PrivacySensitiveLogging(logRequestBody = false, logResponseBody = false)
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	private final AuthService authService;

	@GetMapping("/api/auth/{oauthType}")
	public SuccessResponse<OAuthUrlResponse> redirectAuthRequestUrl(@PathVariable OAuthType oauthType) {
		OAuthUrlResponse response = authService.getAuthCodeRequestUrl(oauthType);
		return SuccessResponse.of(HttpStatus.FOUND, response);
	}

	@PostMapping("/api/auth/login/{oauthType}")
	public SuccessResponse<AuthResponse> login(
		@PathVariable OAuthType oauthType,
		@RequestParam String code,
		@RequestParam(required = false) String name,
		@RequestBody(required = false) LoginRequest request) {

		if (oauthType == OAuthType.APPLE && name != null) {
			// 애플 로그인에서 사용자 정보가 있는 경우
			AuthContext context = new AuthContext(code, name);
			AuthResponse response = authService.loginAndGenerateToken(oauthType, context, request);
			return SuccessResponse.of(response);
		} else {
			// 기존 로직 그대로 사용
			AuthResponse response = authService.loginAndGenerateToken(oauthType, code, request);
			return SuccessResponse.of(response);
		}
	}

	@PostMapping("/api/auth/refresh")
	public SuccessResponse<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
		TokenResponse response = authService.refreshToken(request);
		return SuccessResponse.of(response);
	}

	@SecuredApi
	@PostMapping("/api/auth/logout")
	public SuccessResponse<Void> logout(@RequestBody RefreshTokenRequest request) {
		authService.logout(request.refreshToken());
		return SuccessResponse.ok("로그아웃 성공");
	}

	@SecuredApi
	@PostMapping("/api/auth/withdraw")
	public SuccessResponse<Void> withdraw(@LoginUser User user, @RequestBody @Valid WithdrawRequest request) {
		authService.withdraw(user.getId(), request);
		return SuccessResponse.ok("회원탈퇴 성공");
	}
}
