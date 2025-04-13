package com.evenly.took.global.location.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.evenly.took.global.redis.RedisGeoSpatialService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationRegistrationEventHandler {

	private final RedisGeoSpatialService redisGeoSpatialService;

	@EventListener
	public void handle(LocationRegistrationEvent event) {
		log.info("[이벤트 수신] userId={}, lon={}, lat={}", event.getUserId(), event.getLongitude(), event.getLatitude());

		boolean success = redisGeoSpatialService.registerUserLocation(
			event.getUserId().toString(),
			event.getLongitude(),
			event.getLatitude()
		);

		log.info("[Redis 등록 결과] success={}", success);
	}
}
