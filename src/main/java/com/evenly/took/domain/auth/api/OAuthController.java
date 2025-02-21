package com.evenly.took.domain.auth.api;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.domain.auth.application.OAuthService;
import com.evenly.took.domain.auth.domain.OAuthType;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthController implements OAuthApi {

	private final OAuthService oauthService;

	@GetMapping("/{oauthType}")
	public void redirectAuthRequestUrl(@PathVariable OAuthType oauthType, HttpServletResponse response) throws
		IOException {
		String url = oauthService.getAuthCodeRequestUrl(oauthType);
		response.sendRedirect(url);
	}

	@GetMapping("/login/{oauthType}")
	public void login(@PathVariable OAuthType oauthType, @RequestParam String code) {
		oauthService.loginAndGenerateToken(oauthType, code);
	}
}
