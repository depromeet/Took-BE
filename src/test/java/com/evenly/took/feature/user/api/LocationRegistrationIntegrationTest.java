package com.evenly.took.feature.user.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.integration.JwtMockIntegrationTest;
import com.evenly.took.global.location.event.LocationRegistrationEvent;
import com.evenly.took.global.location.event.LocationRegistrationEventHandler;
import com.evenly.took.global.redis.RedisGeoSpatialService;

@ActiveProfiles("test")
public class LocationRegistrationIntegrationTest extends JwtMockIntegrationTest {

	@Autowired
	private RedisGeoSpatialService redisGeoSpatialService;

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

}
