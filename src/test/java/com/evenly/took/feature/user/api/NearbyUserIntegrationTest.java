package com.evenly.took.feature.user.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.integration.JwtMockIntegrationTest;
import com.evenly.took.global.redis.RedisGeoSpatialService;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

class NearbyUserIntegrationTest extends JwtMockIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(NearbyUserIntegrationTest.class);
	@Autowired
	RedisGeoSpatialService redisGeoSpatialService;

	private static final String[] LOCATION_HEADERS = {
		"37.5,127.0", "37.5005,127.0005", "37.501,127.001", "37.502,127.002",
		"37.503,127.003", "37.504,127.004", "37.505,127.005", "37.506,127.006",
		"37.507,127.007", "37.508,127.008"
	};

	@Test
	void 대표_명함이_있는_유저들_다수_조회된다() {
		// given
		IntStream.range(0, 5).forEach(i -> {
			User user = userFixture.creator()
				.oauthIdentifier(OAuthIdentifier.builder()
					.oauthId("uid_card_" + i)
					.oauthType(OAuthType.GOOGLE)
					.build())
				.create();

			cardFixture.creator()
				.user(user)
				.nickname("유저" + i)
				.imagePath("https://image-path")
				.career(careerFixture.serverDeveloper())
				.isPrimary(true)
				.create();

			String[] parts = LOCATION_HEADERS[i].split(",");
			double lat = Double.parseDouble(parts[0]);
			double lon = Double.parseDouble(parts[1]);
			redisGeoSpatialService.registerUserLocation(String.valueOf(user.getId()), lon, lat);
		});

		// when
		ExtractableResponse<Response> response = given()
			.header("Authorization", authToken)
			.header("x-redis-geo", LOCATION_HEADERS[0])
			.when()
			.get("/api/user/nearby")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();

		// then
		List<?> profiles = response.jsonPath().getList("data.profiles");
		assertThat(profiles).hasSize(5);
	}

	@Test
	void 대표_명함이_없는_유저들_다수_조회된다() {
		// given
		IntStream.range(5, 10).forEach(i -> {
			User user = userFixture.creator()
				.oauthIdentifier(OAuthIdentifier.builder()
					.oauthId("uid_basic_" + i)
					.oauthType(OAuthType.APPLE)
					.build())
				.create();

			String[] parts = LOCATION_HEADERS[i].split(",");
			double lat = Double.parseDouble(parts[0]);
			double lon = Double.parseDouble(parts[1]);
			redisGeoSpatialService.registerUserLocation(String.valueOf(user.getId()), lon, lat);
		});

		// when
		ExtractableResponse<Response> response = given()
			.header("Authorization", authToken)
			.header("x-redis-geo", LOCATION_HEADERS[5])
			.when()
			.get("/api/user/nearby")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();

		// then
		List<?> profiles = response.jsonPath().getList("data.profiles");
		assertThat(profiles).hasSize(5);
	}

	@Test
	void 위치_정보가_없는_헤더는_조회되지_않는다() {
		// given, when
		ExtractableResponse<Response> response = given()
			.header("Authorization", authToken)
			.when()
			.get("/api/user/nearby")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();

		assertThat(response.jsonPath().getList("data.profiles")).isEmpty();
	}

	@Test
	void 위치가_멀면_조회되지_않는다() {
		// given
		User farUser = userFixture.creator()
			.oauthIdentifier(OAuthIdentifier.builder()
				.oauthId("far")
				.oauthType(OAuthType.KAKAO)
				.build())
			.create();

		redisGeoSpatialService.registerUserLocation(String.valueOf(farUser.getId()), 128.0, 38.0);

		// when
		ExtractableResponse<Response> response = given()
			.header("Authorization", authToken)
			.header("x-redis-geo", LOCATION_HEADERS[0])
			.when()
			.get("/api/user/nearby")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();

		// then
		assertThat(response.jsonPath().getList("data.profiles")).isEmpty();
	}

	@Test
	void 대표명함과_기본정보가_혼합된_유저들이_다수_조회된다() {
		// given
		IntStream.range(0, 5).forEach(i -> {
			User user = userFixture.creator()
				.name("기본유저" + i)
				.oauthIdentifier(OAuthIdentifier.builder()
					.oauthId("uid_basic_" + i)
					.oauthType(OAuthType.APPLE)
					.build())
				.create();

			String[] parts = LOCATION_HEADERS[i].split(",");
			double lat = Double.parseDouble(parts[0]);
			double lon = Double.parseDouble(parts[1]);
			redisGeoSpatialService.registerUserLocation(String.valueOf(user.getId()), lon, lat);
		});

		IntStream.range(5, 7).forEach(i -> {
			User user = userFixture.creator()
				.name("대표유저" + i)
				.oauthIdentifier(OAuthIdentifier.builder()
					.oauthId("uid_card_" + i)
					.oauthType(OAuthType.GOOGLE)
					.build())
				.create();

			cardFixture.creator()
				.user(user)
				.nickname("대표닉네임" + i)
				.imagePath("https://image-path-" + i)
				.career(careerFixture.serverDeveloper())
				.isPrimary(true)
				.create();

			cardFixture.creator()
				.user(user)
				.nickname("일반명함" + i)
				.imagePath("https://non-primary-image-" + i)
				.career(careerFixture.frontendDeveloper())
				.isPrimary(false)
				.create();

			String[] parts = LOCATION_HEADERS[i].split(",");
			double lat = Double.parseDouble(parts[0]);
			double lon = Double.parseDouble(parts[1]);
			redisGeoSpatialService.registerUserLocation(String.valueOf(user.getId()), lon, lat);
		});

		// when
		ExtractableResponse<Response> response = given()
			.header("Authorization", authToken)
			.header("x-redis-geo", LOCATION_HEADERS[3])
			.when()
			.get("/api/user/nearby")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();

		// then
		List<Map<String, Object>> profiles = response.jsonPath().getList("data.profiles");
		assertThat(profiles).hasSize(7);

		List<Map<String, Object>> cardProfiles = profiles.stream()
			.filter(p -> p.containsKey("cardId"))
			.toList();
		assertThat(cardProfiles).hasSize(2);
		assertThat(cardProfiles).extracting(p -> p.get("nickname"))
			.containsExactlyInAnyOrder("대표닉네임5", "대표닉네임6");

		List<Map<String, Object>> basicProfiles = profiles.stream()
			.filter(p -> !p.containsKey("cardId"))
			.toList();
		assertThat(basicProfiles).hasSize(5);
		assertThat(basicProfiles).extracting(p -> p.get("name"))
			.containsExactlyInAnyOrder("기본유저0", "기본유저1", "기본유저2", "기본유저3", "기본유저4");
	}
}
