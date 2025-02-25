package com.evenly.took.global.security.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.evenly.took.feature.auth.api.HeaderHandler;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.exception.auth.oauth.InvalidAccessTokenException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private static final List<String> EXCLUDE_PATHS = List.of(
		"/public",
		"/api/health",
		"/api/oauth/login");

	private final JwtTokenProvider jwtTokenProvider;
	private final UuidTokenProvider uuidTokenProvider;
	private final HeaderHandler headerHandler;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			checkTokens(request, response);
			filterChain.doFilter(request, response);
		} catch (TookException ex) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage()); // TODO 형식 통일
		}
	}

	private void checkTokens(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = headerHandler.resolveAccessToken(request);
		String refreshToken = headerHandler.resolveRefreshToken(request);
		try {
			jwtTokenProvider.validateToken(accessToken);
		} catch (InvalidAccessTokenException ex) {
			checkRefreshToken(refreshToken, response);
		}
	}

	private void checkRefreshToken(String refreshToken, HttpServletResponse response) {
		String userId = uuidTokenProvider.getUserId(refreshToken);
		String accessToken = jwtTokenProvider.generateAccessToken(userId);
		headerHandler.setAuthHeader(response, new TokenResponse(accessToken, refreshToken));
	}
}
