package com.evenly.took.global.security;

import static com.evenly.took.global.exception.auth.jwt.JwtErrorCode.*;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.evenly.took.domain.auth.jwt.JwtTokenProvider;

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
		FilterChain filterChain) throws ServletException, IOException {
		String token = resolveToken(request);
		if (token != null && jwtTokenProvider.validateToken(token)) {
			// TODO: JWT 검증 성공 시 후속 처리를 진행 (현재는 헤더에 AUTHORIZATION 으로 넣음)
			response.setHeader(AUTHORIZATION, jwtTokenProvider.getUserId(token));
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, JWT_UNAUTHORIZED.getMessage());
			return;
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION);
		if (bearerToken != null && bearerToken.startsWith(BEARER)) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
