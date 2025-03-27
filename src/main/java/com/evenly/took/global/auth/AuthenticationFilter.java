package com.evenly.took.global.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.evenly.took.feature.auth.api.HeaderHandler;
import com.evenly.took.feature.auth.application.TokenProvider;
import com.evenly.took.global.exception.ErrorCode;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.exception.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final List<String> EXCLUDE_PATHS = List.of(
		"/swagger-ui",
		"/v3/api-docs",
		"/public",
		"/api/health",
		"/api/auth",
		"/api/card/register",
		"/api/card/open");

	static {
		OBJECT_MAPPER.registerModule(new JavaTimeModule());
	}

	private final HeaderHandler headerHandler;
	private final TokenProvider tokenProvider;

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
			sendError(response, ex.getErrorCode());
		}
	}

	private void checkTokens(HttpServletRequest request) {
		String accessToken = headerHandler.resolveAccessToken(request);
		tokenProvider.validateAccessToken(accessToken);
	}

	private void sendError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
		String body = serialize(ErrorResponse.of(errorCode));
		response.getWriter().write(body);
	}

	private String serialize(ErrorResponse responseBody) throws IOException {
		return OBJECT_MAPPER.writeValueAsString(responseBody);
	}
}
