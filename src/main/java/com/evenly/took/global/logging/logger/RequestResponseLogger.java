package com.evenly.took.global.logging.logger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestResponseLogger {

	private static final Logger log = LoggerFactory.getLogger("request-response-log");
	private final ObjectMapper objectMapper;

	public void logRequest(HttpServletRequest request, String requestBody, String clientIP) {
		try {
			Map<String, Object> logInfo = new HashMap<>();
			logInfo.put("type", "REQUEST");
			logInfo.put("clientIP", clientIP);
			logInfo.put("method", request.getMethod());
			logInfo.put("url", getFullURL(request));
			logInfo.put("body", requestBody);

			log.info(objectMapper.writeValueAsString(logInfo));
		} catch (Exception e) {
			log.error("요청 로깅 중 오류 발생", e);
		}
	}

	public void logRequestWithoutBody(HttpServletRequest request, String clientIP) {
		try {
			Map<String, Object> logInfo = new HashMap<>();
			logInfo.put("type", "REQUEST");
			logInfo.put("clientIP", clientIP);
			logInfo.put("method", request.getMethod());
			logInfo.put("url", getFullURL(request));
			logInfo.put("body", "*** BODY LOGGING DISABLED ***");

			log.info(objectMapper.writeValueAsString(logInfo));
		} catch (Exception e) {
			log.error("요청 로깅 중 오류 발생", e);
		}
	}

	public void logResponse(HttpServletRequest request, HttpServletResponse response,
		String responseBody, long latency) {
		try {
			Map<String, Object> logInfo = new HashMap<>();
			logInfo.put("type", "RESPONSE");
			logInfo.put("clientIP", getClientIP(request));
			logInfo.put("method", request.getMethod());
			logInfo.put("url", getFullURL(request));
			logInfo.put("status", response.getStatus());
			logInfo.put("body", responseBody);
			logInfo.put("latency_ms", latency);

			String logMessage = objectMapper.writeValueAsString(logInfo);
			loggingBySatus(response, logMessage);
		} catch (Exception e) {
			log.error("응답 로깅 중 오류 발생", e);
		}
	}

	public void logResponseWithoutBody(HttpServletRequest request, HttpServletResponse response,
		long latency) {
		try {
			Map<String, Object> logInfo = new HashMap<>();
			logInfo.put("type", "RESPONSE");
			logInfo.put("clientIP", getClientIP(request));
			logInfo.put("method", request.getMethod());
			logInfo.put("url", getFullURL(request));
			logInfo.put("status", response.getStatus());
			logInfo.put("body", "*** BODY LOGGING DISABLED ***");
			logInfo.put("latency_ms", latency);

			String logMessage = objectMapper.writeValueAsString(logInfo);
			loggingBySatus(response, logMessage);
		} catch (Exception e) {
			log.error("응답 로깅 중 오류 발생", e);
		}
	}

	public void logException(HttpServletRequest request, Exception exception,
		int status, long latency) {
		try {
			String clientIP = getClientIP(request);

			Map<String, Object> logInfo = new HashMap<>();
			logInfo.put("type", "EXCEPTION");
			logInfo.put("clientIP", clientIP);
			logInfo.put("method", request.getMethod());
			logInfo.put("url", getFullURL(request));
			logInfo.put("status", status);
			logInfo.put("body", "*** BODY LOGGING DISABLED ***");
			logInfo.put("latency_ms", latency);

			log.error(objectMapper.writeValueAsString(logInfo));
		} catch (Exception e) {
			log.error("예외 로깅 중 추가 오류 발생", e);
		}
	}

	private static void loggingBySatus(final HttpServletResponse response, final String logMessage) {
		int status = response.getStatus();
		if (status >= HttpStatus.OK.value() && status < HttpStatus.MULTIPLE_CHOICES.value()) {
			log.info(logMessage);
		} else if (status >= HttpStatus.MULTIPLE_CHOICES.value() && status < HttpStatus.BAD_REQUEST.value()) {
			log.info(logMessage);
		} else if (status >= HttpStatus.BAD_REQUEST.value() && status < HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			log.warn(logMessage);
		} else {
			log.error(logMessage);
		}
	}

	private String getFullURL(HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		url.append(request.getRequestURL());
		if (request.getQueryString() != null) {
			url.append("?").append(request.getQueryString());
		}
		return url.toString();
	}

	private String getClientIP(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			// X-Forwarded-For 형식: 클라이언트, 프록시1, 프록시2, ...
			return xForwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}
