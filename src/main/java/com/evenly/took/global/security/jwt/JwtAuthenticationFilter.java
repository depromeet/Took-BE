package com.evenly.took.global.security.jwt;

import static com.evenly.took.global.exception.auth.jwt.JwtErrorCode.*;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.evenly.took.global.exception.TookException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String BEARER = "Bearer ";
	public static final String AUTHORIZATION = "Authorization";

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain)
		throws ServletException, IOException {

		try {
			String token = resolveToken(request);

			jwtTokenProvider.validateToken(token);
		} catch (TookException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
			return;
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION);
		if (bearerToken == null || !bearerToken.startsWith(BEARER)) {
			throw new TookException(JWT_UNAUTHORIZED);
		}
		return bearerToken.substring(BEARER.length());
	}
}
