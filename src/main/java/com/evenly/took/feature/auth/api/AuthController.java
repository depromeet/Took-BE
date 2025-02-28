package com.evenly.took.feature.auth.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.auth.application.AuthService;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.auth.dto.response.OAuthUrlResponse;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	private final AuthService authService;

	@GetMapping("/api/auth/{oauthType}")
	public SuccessResponse<OAuthUrlResponse> redirectAuthRequestUrl(@PathVariable OAuthType oauthType) {
		OAuthUrlResponse response = authService.getAuthCodeRequestUrl(oauthType);
		return SuccessResponse.of(HttpStatus.FOUND, response);
	}

	@GetMapping("/api/auth/login/{oauthType}")
	public SuccessResponse<AuthResponse> login(@PathVariable OAuthType oauthType, @RequestParam String code) {
		AuthResponse response = authService.loginAndGenerateToken(oauthType, code);
		return SuccessResponse.of(response);
	}

	@PostMapping("/api/auth/refresh")
	public SuccessResponse<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
		TokenResponse response = authService.refreshToken(request);
		return SuccessResponse.of(response);
	}
}
