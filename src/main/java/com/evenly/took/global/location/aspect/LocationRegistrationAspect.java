package com.evenly.took.global.location.aspect;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.location.event.LocationRegistrationEvent;
import com.evenly.took.global.location.util.LocationHeaderParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LocationRegistrationAspect {

	private static final String X_REDIS_GEO = "x-redis-geo";

	private final ApplicationEventPublisher eventPublisher;

	@AfterReturning(
		pointcut = "execution(* *(..)) && " +
			"(@annotation(com.evenly.took.global.location.meta.RegisterLocation) || " +
			" @within(com.evenly.took.global.location.meta.RegisterLocation)) && " +
			"!@annotation(com.evenly.took.global.auth.meta.PublicApi) && " +
			"!@within(com.evenly.took.global.auth.meta.PublicApi)"
	)
	public void publishLocationEvent(JoinPoint joinPoint) {
		User user = extractUser(joinPoint.getArgs());
		if (user == null) {
			log.warn("[AOP] User 인자 없음 - AOP 중단");
			return;
		}

		HttpServletRequest request = getCurrentHttpRequest();
		if (request == null) {
			log.warn("[AOP] HttpServletRequest 조회 실패 - AOP 중단");
			return;
		}

		String locationHeader = request.getHeader(X_REDIS_GEO);
		if (locationHeader == null || locationHeader.isBlank()) {
			log.debug("[AOP] x-redis-geo 헤더 없음 - 위치 등록 생략");
			return;
		}

		try {
			Point point = LocationHeaderParser.parse(locationHeader);
			LocationRegistrationEvent event = new LocationRegistrationEvent(
				this, user.getId(), point.getX(), point.getY()
			);
			eventPublisher.publishEvent(event);
			log.info("[AOP] 위치 등록 이벤트 발행됨: userId={}, point={}", user.getId(), point);
		} catch (Exception e) {
			log.error("[AOP] 위치 파싱 실패 - header={}, error={}", locationHeader, e.getMessage());
		}
	}

	private User extractUser(Object[] args) {
		for (Object arg : args) {
			if (arg instanceof User) {
				return (User)arg;
			}
		}
		return null;
	}

	private HttpServletRequest getCurrentHttpRequest() {
		return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
			.filter(ServletRequestAttributes.class::isInstance)
			.map(ServletRequestAttributes.class::cast)
			.map(ServletRequestAttributes::getRequest)
			.orElse(null);
	}
}
