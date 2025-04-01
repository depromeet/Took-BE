package com.evenly.took.global.logging.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.evenly.took.global.logging.dto.PrivacySensitiveDataInfo;
import com.evenly.took.global.logging.logger.RequestResponseLogger;
import com.evenly.took.global.logging.meta.PrivacySensitiveLogging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
@RequiredArgsConstructor
public class HttpTraceLoggingFilter extends OncePerRequestFilter {

	private final RequestResponseLogger logger;
	private final RequestMappingHandlerMapping handlerMapping;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		return path.startsWith("/swagger-ui") ||
			path.startsWith("/v3/api-docs") ||
			path.startsWith("/favicon.ico") ||
			path.startsWith("/static/") ||
			path.startsWith("/error") ||
			path.startsWith("/resources/") ||
			path.startsWith("/assets/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		ContentCachingRequestWrapper requestWrapper;
		ContentCachingResponseWrapper responseWrapper;

		if (request instanceof ContentCachingRequestWrapper) {
			requestWrapper = (ContentCachingRequestWrapper)request;
		} else {
			requestWrapper = new ContentCachingRequestWrapper(request);
		}

		if (response instanceof ContentCachingResponseWrapper) {
			responseWrapper = (ContentCachingResponseWrapper)response;
		} else {
			responseWrapper = new ContentCachingResponseWrapper(response);
		}

		String clientIP = getClientIP(requestWrapper);
		long startTime = System.currentTimeMillis();

		PrivacySensitiveDataInfo privacyInfo = getPrivacySensitiveDataInfo(requestWrapper);

		try {
			// 요청 로깅 (필터 체인 실행 전)
			if (privacyInfo.logRequestBody()) {
				logRequest(requestWrapper, clientIP);
			} else {
				logger.logRequestWithoutBody(requestWrapper, clientIP);
			}

			filterChain.doFilter(requestWrapper, responseWrapper);

			// 응답 로깅 (필터 체인 실행 후)
			long duration = System.currentTimeMillis() - startTime;
			if (privacyInfo.logResponseBody()) {
				logResponse(requestWrapper, responseWrapper, duration);
			} else {
				logger.logResponseWithoutBody(requestWrapper, responseWrapper, duration);
			}

		} catch (Exception e) {
			long duration = System.currentTimeMillis() - startTime;
			logger.logException(requestWrapper, e, HttpStatus.INTERNAL_SERVER_ERROR.value(), duration);
			throw e;
		} finally {
			if (!responseWrapper.isCommitted()) {
				responseWrapper.copyBodyToResponse();
			}
		}
	}

	private PrivacySensitiveDataInfo getPrivacySensitiveDataInfo(HttpServletRequest request) {
		try {
			// 현재 요청을 처리할 핸들러(컨트롤러 메서드) 찾기
			HandlerExecutionChain handlerChain = handlerMapping.getHandler(request);

			if (handlerChain != null && handlerChain.getHandler() instanceof HandlerMethod) {
				HandlerMethod handlerMethod = (HandlerMethod)handlerChain.getHandler();

				Method method = handlerMethod.getMethod();
				Class<?> controllerClass = handlerMethod.getBeanType();

				// 메서드 레벨 어노테이션 확인 (우선순위 높음)
				PrivacySensitiveLogging methodAnnotation = method.getAnnotation(PrivacySensitiveLogging.class);
				if (methodAnnotation != null) {
					return new PrivacySensitiveDataInfo(
						methodAnnotation.logRequestBody(),
						methodAnnotation.logResponseBody()
					);
				}

				// 클래스 레벨 어노테이션 확인
				PrivacySensitiveLogging classAnnotation = controllerClass.getAnnotation(PrivacySensitiveLogging.class);
				if (classAnnotation != null) {
					return new PrivacySensitiveDataInfo(
						classAnnotation.logRequestBody(),
						classAnnotation.logResponseBody()
					);
				}
			}
		} catch (Exception e) {
			log.warn("핸들러 매핑 중 오류 발생: {}", e.getMessage());
		}

		// 기본적으로 모든 내용 로깅
		return new PrivacySensitiveDataInfo(true, true);
	}

	private void logRequest(ContentCachingRequestWrapper request, String clientIP) {
		byte[] content = request.getContentAsByteArray();
		if (content.length > 0) {
			String requestBody = new String(content, StandardCharsets.UTF_8);
			logger.logRequest(request, requestBody, clientIP);
		} else {
			logger.logRequest(request, "", clientIP);
		}
	}

	private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper response, long duration) {
		byte[] content = response.getContentAsByteArray();
		String responseBody = new String(content, StandardCharsets.UTF_8);
		logger.logResponse(request, response, responseBody, duration);
	}

	private String getClientIP(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}
