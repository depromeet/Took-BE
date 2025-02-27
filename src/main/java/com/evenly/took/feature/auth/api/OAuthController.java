package com.evenly.took.feature.auth.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.auth.application.OAuthService;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.auth.dto.response.OAuthUrlResponse;
import com.evenly.took.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OAuthController implements OAuthApi {

	private final OAuthService oauthService;

	@GetMapping("/api/oauth/{oauthType}")
	public SuccessResponse<OAuthUrlResponse> redirectAuthRequestUrl(@PathVariable OAuthType oauthType) {
		OAuthUrlResponse response = oauthService.getAuthCodeRequestUrl(oauthType);
		return SuccessResponse.of(HttpStatus.FOUND, response);
	}

	@GetMapping("/api/oauth/login/{oauthType}")
	public SuccessResponse<AuthResponse> login(@PathVariable OAuthType oauthType, @RequestParam String code) {
		AuthResponse authResponse = oauthService.loginAndGenerateToken(oauthType, code);
		return SuccessResponse.of(authResponse);
	}
}
