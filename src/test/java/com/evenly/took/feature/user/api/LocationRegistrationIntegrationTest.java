package com.evenly.took.feature.user.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.global.integration.JwtMockIntegrationTest;
import com.evenly.took.global.location.event.LocationRegistrationEvent;
import com.evenly.took.global.location.event.LocationRegistrationEventHandler;
import com.evenly.took.global.redis.RedisGeoSpatialService;

import jakarta.persistence.EntityManager;

@ActiveProfiles("test")
public class LocationRegistrationIntegrationTest extends JwtMockIntegrationTest {

	@Autowired
	private RedisGeoSpatialService redisGeoSpatialService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private EntityManager entityManager;

	@AfterEach
	void cleanUp() {
		redisGeoSpatialService.removeUserLocation(String.valueOf(mockUser.getId()));
	}

	@Test
	void geohash_없을때_등록되지않음() {
		given()
			.header("Authorization", authToken)
			.when()
			.get("/api/user/nearby")
			.then()
			.statusCode(HttpStatus.OK.value());

		Point position = redisGeoSpatialService.getUserPosition(String.valueOf(mockUser.getId()));
		assertThat(position).isNull();
	}

	@Test
	void 잘못된_geohash_예외처리() {
		String invalidGeoHash = "invalidHash";

		given()
			.header("Authorization", authToken)
			.header("x-redis-geo", invalidGeoHash)
			.when()
			.get("/api/user/nearby")
			.then()
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		Point position = redisGeoSpatialService.getUserPosition(String.valueOf(mockUser.getId()));
		assertThat(position).isNull();
	}

	@Test
	void aop_진입_확인() {
		String geoHash = "127.0,37.5";

		given()
			.header("Authorization", authToken)
			.header("x-redis-geo", geoHash)
			.when()
			.get("/api/health")
			.then()
			.statusCode(HttpStatus.OK.value());
	}

	@Test
	void 이벤트_핸들러_직접_호출() {
		LocationRegistrationEventHandler handler = new LocationRegistrationEventHandler(redisGeoSpatialService);

		LocationRegistrationEvent event = new LocationRegistrationEvent(
			this, mockUser.getId(), 127.0, 37.56
		);

		handler.handle(event);

		Point position = redisGeoSpatialService.getUserPosition(String.valueOf(mockUser.getId()));
		assertThat(position).isNotNull();
	}

	@Test
	void redis_위치등록_단독() {
		boolean result = redisGeoSpatialService.registerUserLocation(
			String.valueOf(mockUser.getId()), 127.0, 37.56
		);

		assertThat(result).isTrue();

		Point point = redisGeoSpatialService.getUserPosition(String.valueOf(mockUser.getId()));
		assertThat(point).isNotNull();
	}

	/**
	 * 이벤트 발행 전, 엔티티 매니저를 통해 변경사항을 flush
	 * (필요시 entityManager.clear()도 함께 호출)
	 * 이렇게 하면 트랜잭션 내 변경사항이 DB에 반영되고, 커밋 시점에 AFTER_COMMIT 이벤트가 발생합니다.
	 *
	 * @Commit -> 트랜잭션 커밋을 강제하여 AFTER_COMMIT 이벤트가 발생하게 함
	 */
	@Transactional
	@Commit // 실제 커밋하여 AFTER_COMMIT 단계가 발생하도록 함
	@Test
	void 비동기_트랜잭셔널_이벤트_실행_확인() throws InterruptedException {
		// 이벤트 발행
		LocationRegistrationEvent event = new LocationRegistrationEvent(
			this, mockUser.getId(), 127.0, 37.56
		);
		publisher.publishEvent(event);

		// 엔티티 매니저의 flush()를 호출하여 현재 트랜잭션의 변경사항을 DB에 반영하고, 커밋 시 AFTER_COMMIT 이벤트가 발생하도록 함
		entityManager.flush();

		Thread.sleep(2000);

		// [이벤트 수신] userId=1, lon=127.0, lat=37.56
	}
}
