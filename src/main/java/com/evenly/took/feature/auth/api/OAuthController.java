package com.evenly.took.feature.auth.api;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.auth.application.OAuthService;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.global.response.SuccessResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OAuthController implements OAuthApi {

	private final OAuthService oauthService;
	private final HeaderProvider headerProvider;

	@GetMapping("/api/oauth/{oauthType}")
	public void redirectAuthRequestUrl(
		@PathVariable OAuthType oauthType, HttpServletResponse response) throws IOException {
		String url = oauthService.getAuthCodeRequestUrl(oauthType);
		response.sendRedirect(url);
	}

	@GetMapping("/api/oauth/login/{oauthType}")
	public SuccessResponse login(
		@PathVariable OAuthType oauthType, @RequestParam String code, HttpServletResponse response) {
		AuthResponse authResponse = oauthService.loginAndGenerateToken(oauthType, code);
		headerProvider.setAuthHeader(response, authResponse);
		return SuccessResponse.of(authResponse.user());
	}
}
