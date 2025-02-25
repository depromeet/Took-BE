package com.evenly.took.global.security.jwt;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.evenly.took.feature.auth.api.HeaderHandler;
import com.evenly.took.feature.auth.application.UuidTokenProvider;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.global.exception.auth.oauth.InvalidAccessTokenException;
import com.evenly.took.global.exception.auth.oauth.InvalidRefreshTokenException;
import com.evenly.took.global.exception.auth.oauth.InvalidTokenException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UuidTokenProvider uuidTokenProvider;
	private final HeaderHandler headerHandler;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			checkTokens(request, response);
			filterChain.doFilter(request, response);
		} catch (InvalidTokenException | InvalidRefreshTokenException ex) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage()); // TODO 형식 통일
		}
	}

	private void checkTokens(HttpServletRequest request, HttpServletResponse response)
		throws InvalidTokenException, InvalidRefreshTokenException {

		String accessToken = headerHandler.resolveAccessToken(request);
		String refreshToken = headerHandler.resolveRefreshToken(request);

		try {
			jwtTokenProvider.validateToken(accessToken);
		} catch (InvalidAccessTokenException ex) {
			String userId = jwtTokenProvider.getUserId(accessToken);
			checkRefreshToken(userId, refreshToken, response);
		}
	}

	private void checkRefreshToken(String userId, String refreshToken, HttpServletResponse response)
		throws InvalidRefreshTokenException {

		uuidTokenProvider.validateToken(userId, refreshToken);
		String accessToken = jwtTokenProvider.generateAccessToken(userId);
		headerHandler.setAuthHeader(response, new TokenResponse(accessToken, refreshToken));
	}
}
