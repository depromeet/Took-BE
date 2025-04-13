package com.evenly.took.global.logging.filter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import com.evenly.took.feature.user.dao.BlacklistedIPRepository;
import com.evenly.took.feature.user.domain.BlacklistedIP;
import com.evenly.took.feature.user.domain.vo.RequestCounter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);
	private static final Logger blockingLog = LoggerFactory.getLogger("blocking-log");

	private final BlacklistedIPRepository blacklistedIPRepository;
	private final int maxRequestsPerWindow;
	private final int timeWindowSeconds;

	private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
	private final Map<String, Boolean> blacklistCache = new ConcurrentHashMap<>();

	private static final int CACHE_CLEANUP_FREQUENCY = 1000;
	private final AtomicInteger requestCount = new AtomicInteger(0);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String clientIP = getClientIP(request);
		String requestUri = request.getRequestURI();

		if (isExcludedPath(requestUri)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (isBlacklistedInCache(clientIP)) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("접근이 차단되었습니다.");
			return;
		}

		if (isBlacklistedInDatabase(clientIP)) {
			logBlockedIP(clientIP, requestUri, "1초에 100개 이상 요청된 이상한 IP");
			blacklistCache.put(clientIP, Boolean.TRUE);
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("접근이 차단되었습니다.");
			return;
		}

		if (isRateLimited(clientIP)) {
			String blockReason = "속도 제한 초과로 영구 블랙리스트에 추가";
			logBlockedIP(clientIP, requestUri, blockReason);
			addToBlacklistDatabase(clientIP, blockReason);
			blacklistCache.put(clientIP, Boolean.TRUE);
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("접근이 차단되었습니다.");
			return;
		}

		try {
			filterChain.doFilter(request, response);
		} finally {
			if (requestCount.incrementAndGet() % CACHE_CLEANUP_FREQUENCY == 0) {
				cleanupCache();
				requestCount.set(0);
			}
		}
	}

	private void logBlockedIP(String clientIP, String requestUri, String reason) {
		// 전용 로거를 사용하여 차단된 IP 로깅
		blockingLog.warn("IP 차단 - IP: {}, URI: {}, 사유: {}", clientIP, requestUri, reason);
	}

	private boolean isBlacklistedInCache(String clientIP) {
		return Boolean.TRUE.equals(blacklistCache.get(clientIP));
	}

	private boolean isBlacklistedInDatabase(String clientIP) {
		try {
			Optional<BlacklistedIP> blacklistedIP = blacklistedIPRepository.findByIpAddress(clientIP);
			return blacklistedIP.isPresent();
		} catch (Exception e) {
			log.error("블랙리스트 DB 조회 중 오류 발생", e);
			return false;
		}
	}

	@Transactional
	public void addToBlacklistDatabase(String clientIP, String reason) {
		try {
			if (!blacklistedIPRepository.findByIpAddress(clientIP).isPresent()) {
				BlacklistedIP newBlacklistedIP = BlacklistedIP.of(clientIP, reason);
				blacklistedIPRepository.save(newBlacklistedIP);
				log.info("IP {} 영구 차단됨: {}", clientIP, reason);
			} else {
				log.info("IP {} 이미 차단된 상태: {}", clientIP, reason);
			}
		} catch (Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				log.warn("IP {} 블랙리스트 추가 시 동시성 충돌 발생 (이미 추가됨)", clientIP);
			} else {
				log.error("블랙리스트 DB 추가 중 오류 발생", e);
			}
		}
	}

	private boolean isRateLimited(String clientIP) {
		long currentTime = System.currentTimeMillis();
		RequestCounter counter = requestCounters.computeIfAbsent(clientIP,
			k -> new RequestCounter(currentTime));

		log.debug("IP: {}, 처리 전 카운트: {}, 최대 요청: {}, 시간 윈도우: {}초",
			clientIP, counter.getCount().get(), maxRequestsPerWindow, timeWindowSeconds);

		if (currentTime - counter.getWindowStart() > timeWindowSeconds * 1000L) {
			counter.resetWindow(currentTime);
			log.debug("IP: {} 윈도우 리셋됨, 새 카운트: {}", clientIP, counter.getCount().get());
		}

		int count = counter.incrementAndGet();
		log.debug("IP: {}, 증가 후 카운트: {}, 제한 초과 여부: {}",
			clientIP, count, count > maxRequestsPerWindow);

		return count > maxRequestsPerWindow;
	}

	private void cleanupCache() {
		long currentTime = System.currentTimeMillis();

		requestCounters.entrySet().removeIf(entry ->
			currentTime - entry.getValue().getLastActivityTime() > timeWindowSeconds * 5L * 1000);

		log.debug("캐시 정리 완료: {} 요청 카운터", requestCounters.size());
	}

	/**
	 * TODO: 필요한지 확인
	 */
	private boolean isExcludedPath(String uri) {
		return uri.startsWith("/static/") ||
			uri.startsWith("/favicon.ico") ||
			uri.startsWith("/resources/") ||
			uri.startsWith("/assets/");
	}

	private String getClientIP(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			// X-Forwarded-For에는 프록시 체인으로 인해 여러 IP가 있을 수 있음
			// 첫 번째 IP가 원래 클라이언트 IP
			return xForwardedFor.split(",")[0].trim();
		}

		return request.getRemoteAddr();
	}
}
