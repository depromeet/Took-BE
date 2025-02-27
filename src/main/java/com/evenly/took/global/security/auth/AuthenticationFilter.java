package com.evenly.took.global.security.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.evenly.took.feature.auth.api.HeaderHandler;
import com.evenly.took.feature.auth.application.TokenProvider;
import com.evenly.took.global.exception.TookException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private static final List<String> EXCLUDE_PATHS = List.of(
		"/swagger-ui",
		"/v3/api-docs",
		"/public",
		"/api/health",
		"/api/oauth");

	private final TokenProvider tokenProvider;
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
			checkTokens(request);
			filterChain.doFilter(request, response);
		} catch (TookException ex) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage()); // TODO 형식 통일
		}
	}

	private void checkTokens(HttpServletRequest request) {
		String accessToken = headerHandler.resolveAccessToken(request);
		tokenProvider.validateAccessToken(accessToken);
	}
}
