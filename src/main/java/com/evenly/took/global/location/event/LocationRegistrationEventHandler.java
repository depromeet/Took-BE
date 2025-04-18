package com.evenly.took.global.location.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.evenly.took.global.redis.RedisGeoSpatialService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationRegistrationEventHandler {

	private final RedisGeoSpatialService redisGeoSpatialService;

	@Async("LocationAsyncExecutor")
	@TransactionalEventListener(
		phase = TransactionPhase.AFTER_COMMIT,
		fallbackExecution = true
	)
	public void handle(LocationRegistrationEvent event) {
		log.info("[이벤트 수신] userId={}, lon={}, lat={}", event.getUserId(), event.getLongitude(), event.getLatitude());

		boolean success = redisGeoSpatialService.registerUserLocation(
			event.getUserId().toString(),
			event.getLongitude(),
			event.getLatitude()
		);

		log.info("[Redis 등록/갱신 결과] success={}", success);
	}
}
