package com.evenly.took.global.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.evenly.took.feature.auth.api.HeaderHandler;
import com.evenly.took.feature.auth.application.TokenProvider;
import com.evenly.took.global.auth.meta.PublicApi;
import com.evenly.took.global.auth.meta.SecuredApi;
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
	private static final List<String> ALWAYS_EXCLUDE_PATHS = List.of(
		"/swagger-ui",
		"/v3/api-docs",
		"/api/health"
	);

	static {
		OBJECT_MAPPER.registerModule(new JavaTimeModule());
	}

	private final HeaderHandler headerHandler;
	private final TokenProvider tokenProvider;
	private final RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		if (ALWAYS_EXCLUDE_PATHS.stream().anyMatch(path::startsWith)) {
			return true;
		}

		try {
			Object handler = requestMappingHandlerMapping.getHandler(request).getHandler();

			if (handler instanceof HandlerMethod) {
				HandlerMethod handlerMethod = (HandlerMethod)handler;

				if (handlerMethod.getMethod().isAnnotationPresent(SecuredApi.class)) {
					return false;
				}

				if (handlerMethod.getMethod().isAnnotationPresent(PublicApi.class)) {
					return true;
				}

				if (handlerMethod.getBeanType().isAnnotationPresent(PublicApi.class)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.debug("Error determining handler for request: " + e.getMessage());
		}

		return false;
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