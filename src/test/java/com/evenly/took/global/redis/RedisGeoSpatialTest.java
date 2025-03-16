package com.evenly.took.global.redis;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.config.testcontainers.RedisTestConfig;
import com.evenly.took.global.redis.dto.NearbyUserDto;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class RedisGeoSpatialTest {

	@Autowired
	private RedisGeoSpatialService redisGeoSpatialService;

	private final String TEST_SESSION_ID = "test-precision-session";

	// 37.5065, 127.0548 - 한 건물 좌표
	private final double BASE_LONGITUDE = 127.0548;
	private final double BASE_LATITUDE = 37.5065;

	/**
	 * 작은 거리 변화를 위한 변환 계수
	 * 위도 1도 = 약 111km
	 * 경도 1도 = 위도에 따라 다르지만 서울 위치(약 37.5도)에서 약 88.74km
	 */
	private final double METER_TO_LATITUDE_DEGREE = 1.0 / 111000.0;  // 1m = 약 0.000009도(위도)
	private final double METER_TO_LONGITUDE_DEGREE = 1.0 / 88740.0;  // 1m = 약 0.000011도(경도)

	private final String CENTER_USER = "center_user";
	private final String USER_10M_AWAY = "user_10m";
	private final String USER_1M_AWAY = "user_1m";
	private final String USER_10CM_AWAY = "user_10cm";
	private final String USER_EXACT_SAME = "user_same";

	@BeforeEach
	void setUp() {
		cleanUpTestData();
	}

	@AfterEach
	void tearDown() {
		cleanUpTestData();
	}

	private void cleanUpTestData() {
		// 테스트 데이터 삭제
		redisGeoSpatialService.removeUserLocation(CENTER_USER, TEST_SESSION_ID);
		redisGeoSpatialService.removeUserLocation(USER_10M_AWAY, TEST_SESSION_ID);
		redisGeoSpatialService.removeUserLocation(USER_1M_AWAY, TEST_SESSION_ID);
		redisGeoSpatialService.removeUserLocation(USER_10CM_AWAY, TEST_SESSION_ID);
		redisGeoSpatialService.removeUserLocation(USER_EXACT_SAME, TEST_SESSION_ID);
	}

	@Nested
	@DisplayName("사용자 위치 등록 테스트")
	class 사용자_위치_등록_테스트 {

		@Test
		void 기준_위치_사용자_등록() {
			// when
			boolean result = redisGeoSpatialService.registerUserLocation(
				CENTER_USER, BASE_LONGITUDE, BASE_LATITUDE, TEST_SESSION_ID);

			// then
			assertThat(result).isTrue();

			Point position = redisGeoSpatialService.getUserPosition(CENTER_USER, TEST_SESSION_ID);
			assertThat(position).isNotNull();
			assertThat(position.getX()).isCloseTo(BASE_LONGITUDE, within(0.0001));
			assertThat(position.getY()).isCloseTo(BASE_LATITUDE, within(0.0001));
		}

		@Test
		void 여러_위치의_사용자_등록() {
			// when
			boolean centerResult = redisGeoSpatialService.registerUserLocation(
				CENTER_USER, BASE_LONGITUDE, BASE_LATITUDE, TEST_SESSION_ID);

			// 10m 떨어진 위치 (북동쪽)
			double lon10m = BASE_LONGITUDE + (10 * METER_TO_LONGITUDE_DEGREE);
			double lat10m = BASE_LATITUDE + (10 * METER_TO_LATITUDE_DEGREE);
			boolean result10m = redisGeoSpatialService.registerUserLocation(
				USER_10M_AWAY, lon10m, lat10m, TEST_SESSION_ID);

			// 1m 떨어진 위치 (동쪽)
			double lon1m = BASE_LONGITUDE + (1 * METER_TO_LONGITUDE_DEGREE);
			double lat1m = BASE_LATITUDE;
			boolean result1m = redisGeoSpatialService.registerUserLocation(
				USER_1M_AWAY, lon1m, lat1m, TEST_SESSION_ID);

			// 10cm 떨어진 위치 (남쪽)
			double lon10cm = BASE_LONGITUDE;
			double lat10cm = BASE_LATITUDE - (0.1 * METER_TO_LATITUDE_DEGREE);
			boolean result10cm = redisGeoSpatialService.registerUserLocation(
				USER_10CM_AWAY, lon10cm, lat10cm, TEST_SESSION_ID);

			// then
			assertThat(centerResult).isTrue();
			assertThat(result10m).isTrue();
			assertThat(result1m).isTrue();
			assertThat(result10cm).isTrue();
		}

		@Test
		void 정확히_같은_위치에_여러_사용자_등록() {
			// when
			boolean centerResult = redisGeoSpatialService.registerUserLocation(
				CENTER_USER, BASE_LONGITUDE, BASE_LATITUDE, TEST_SESSION_ID);

			boolean sameResult = redisGeoSpatialService.registerUserLocation(
				USER_EXACT_SAME, BASE_LONGITUDE, BASE_LATITUDE, TEST_SESSION_ID);

			// then
			assertThat(centerResult).isTrue();
			assertThat(sameResult).isTrue();

			// 두 사용자의 위치가 정확히 같은지 확인
			Point position1 = redisGeoSpatialService.getUserPosition(CENTER_USER, TEST_SESSION_ID);
			Point position2 = redisGeoSpatialService.getUserPosition(USER_EXACT_SAME, TEST_SESSION_ID);

			assertThat(position1.getX()).isCloseTo(position2.getX(), within(0.0001));
			assertThat(position1.getY()).isCloseTo(position2.getY(), within(0.0001));

			// 두 사용자 간 거리 확인 (0에 매우 가까워야 함)
			double distance = redisGeoSpatialService.calculateDistance(CENTER_USER, USER_EXACT_SAME,
				TEST_SESSION_ID);
			assertThat(distance).isLessThan(0.1);  // 10cm 미만(오차범위 감안)
		}
	}

	@Nested
	class 사용자_위치_조회_테스트 {

		@BeforeEach
		void setUpLocationData() {
			// 기준 위치 사용자
			redisGeoSpatialService.registerUserLocation(
				CENTER_USER, BASE_LONGITUDE, BASE_LATITUDE, TEST_SESSION_ID);

			// 10m 떨어진 위치 (북동쪽)
			double lon10m = BASE_LONGITUDE + (10 * METER_TO_LONGITUDE_DEGREE);
			double lat10m = BASE_LATITUDE + (10 * METER_TO_LATITUDE_DEGREE);
			redisGeoSpatialService.registerUserLocation(
				USER_10M_AWAY, lon10m, lat10m, TEST_SESSION_ID);

			// 1m 떨어진 위치 (동쪽)
			double lon1m = BASE_LONGITUDE + (1 * METER_TO_LONGITUDE_DEGREE);
			double lat1m = BASE_LATITUDE;
			redisGeoSpatialService.registerUserLocation(
				USER_1M_AWAY, lon1m, lat1m, TEST_SESSION_ID);

			// 10cm 떨어진 위치 (남쪽)
			double lon10cm = BASE_LONGITUDE;
			double lat10cm = BASE_LATITUDE - (0.1 * METER_TO_LATITUDE_DEGREE);
			redisGeoSpatialService.registerUserLocation(
				USER_10CM_AWAY, lon10cm, lat10cm, TEST_SESSION_ID);
		}

		@Test
		void 사용자_위치_정확히_조회() {
			// when
			Point centerPosition = redisGeoSpatialService.getUserPosition(CENTER_USER, TEST_SESSION_ID);
			Point position10m = redisGeoSpatialService.getUserPosition(USER_10M_AWAY, TEST_SESSION_ID);
			Point position1m = redisGeoSpatialService.getUserPosition(USER_1M_AWAY, TEST_SESSION_ID);
			Point position10cm = redisGeoSpatialService.getUserPosition(USER_10CM_AWAY, TEST_SESSION_ID);

			// then
			assertThat(centerPosition).isNotNull();
			assertThat(centerPosition.getX()).isCloseTo(BASE_LONGITUDE, within(0.0001));
			assertThat(centerPosition.getY()).isCloseTo(BASE_LATITUDE, within(0.0001));

			assertThat(position10m).isNotNull();
			assertThat(position10m.getX()).isCloseTo(BASE_LONGITUDE + (10 * METER_TO_LONGITUDE_DEGREE),
				within(0.0001));
			assertThat(position10m.getY()).isCloseTo(BASE_LATITUDE + (10 * METER_TO_LATITUDE_DEGREE),
				within(0.0001));

			assertThat(position1m).isNotNull();
			assertThat(position1m.getX()).isCloseTo(BASE_LONGITUDE + (1 * METER_TO_LONGITUDE_DEGREE),
				within(0.0001));
			assertThat(position1m.getY()).isCloseTo(BASE_LATITUDE, within(0.0001));

			assertThat(position10cm).isNotNull();
			assertThat(position10cm.getX()).isCloseTo(BASE_LONGITUDE, within(0.0001));
			assertThat(position10cm.getY()).isCloseTo(BASE_LATITUDE - (0.1 * METER_TO_LATITUDE_DEGREE),
				within(0.0001));
		}

		@Test
		void 두_사용자_간_거리_계산() {
			// when
			double distanceTo10m = redisGeoSpatialService.calculateDistance(CENTER_USER, USER_10M_AWAY,
				TEST_SESSION_ID);
			double distanceTo1m = redisGeoSpatialService.calculateDistance(CENTER_USER, USER_1M_AWAY,
				TEST_SESSION_ID);
			double distanceTo10cm = redisGeoSpatialService.calculateDistance(CENTER_USER, USER_10CM_AWAY,
				TEST_SESSION_ID);

			// then
			assertThat(distanceTo10m).isBetween(8.0, 16.0);  // 약 10m 허용 오차 포함
			assertThat(distanceTo1m).isBetween(0.7, 1.6);    // 약 1m 허용 오차 포함
			assertThat(distanceTo10cm).isBetween(0.05, 0.3); // 약 10cm 허용 오차 포함
		}
	}

	@Nested
	class 주변_사용자_검색_테스트 {

		@BeforeEach
		void setUpLocationData() {
			// 기준 위치 사용자
			redisGeoSpatialService.registerUserLocation(
				CENTER_USER, BASE_LONGITUDE, BASE_LATITUDE, TEST_SESSION_ID);

			// 10m 떨어진 위치 (북동쪽)
			double lon10m = BASE_LONGITUDE + (10 * METER_TO_LONGITUDE_DEGREE);
			double lat10m = BASE_LATITUDE + (10 * METER_TO_LATITUDE_DEGREE);
			redisGeoSpatialService.registerUserLocation(
				USER_10M_AWAY, lon10m, lat10m, TEST_SESSION_ID);

			// 1m 떨어진 위치 (동쪽)
			double lon1m = BASE_LONGITUDE + (1 * METER_TO_LONGITUDE_DEGREE);
			double lat1m = BASE_LATITUDE;
			redisGeoSpatialService.registerUserLocation(
				USER_1M_AWAY, lon1m, lat1m, TEST_SESSION_ID);

			// 10cm 떨어진 위치 (남쪽)
			double lon10cm = BASE_LONGITUDE;
			double lat10cm = BASE_LATITUDE - (0.1 * METER_TO_LATITUDE_DEGREE);
			redisGeoSpatialService.registerUserLocation(
				USER_10CM_AWAY, lon10cm, lat10cm, TEST_SESSION_ID);
		}

		@Test
		void 반경_15m_내_사용자_검색() {
			// when
			List<NearbyUserDto> nearbyUsers = redisGeoSpatialService.findNearbyUsers(
				BASE_LONGITUDE, BASE_LATITUDE, 15.0, TEST_SESSION_ID);

			// then
			assertThat(nearbyUsers).hasSize(4); // 모든 사용자가 15m 내에 있음

			// 거리순 정렬 확인
			assertThat(nearbyUsers.get(0).userId()).isEqualTo(CENTER_USER);
			assertThat(nearbyUsers.get(1).userId()).isEqualTo(USER_10CM_AWAY);
			assertThat(nearbyUsers.get(2).userId()).isEqualTo(USER_1M_AWAY);
			assertThat(nearbyUsers.get(3).userId()).isEqualTo(USER_10M_AWAY);

			// 대략적인 거리 확인
			assertThat(nearbyUsers.get(0).distance()).isLessThan(0.2); // 자기 자신과의 거리는 거의 0
			assertThat(nearbyUsers.get(1).distance()).isBetween(0.05, 0.3); // 약 10cm
			assertThat(nearbyUsers.get(2).distance()).isBetween(0.7, 1.6);  // 약 1m
			assertThat(nearbyUsers.get(3).distance()).isBetween(8.0, 16.0); // 약 10m
		}

		@Test
		void 반경_5m_내_사용자_검색() {
			// when
			List<NearbyUserDto> nearbyUsers = redisGeoSpatialService.findNearbyUsers(
				BASE_LONGITUDE, BASE_LATITUDE, 5.0, TEST_SESSION_ID);

			// then
			assertThat(nearbyUsers).hasSize(3);

			assertThat(nearbyUsers).extracting(NearbyUserDto::userId)
				.containsExactly(CENTER_USER, USER_10CM_AWAY, USER_1M_AWAY);
		}

		@Test
		void 반경_50cm_내_사용자_검색() {
			// when
			List<NearbyUserDto> nearbyUsers = redisGeoSpatialService.findNearbyUsers(
				BASE_LONGITUDE, BASE_LATITUDE, 0.5, TEST_SESSION_ID);

			// then
			assertThat(nearbyUsers).hasSize(2); // 1m, 10m 떨어진 사용자는 제외

			assertThat(nearbyUsers).extracting(NearbyUserDto::userId)
				.containsExactly(CENTER_USER, USER_10CM_AWAY);
		}

		/**
		 * 설정에 의하면 15cm로 설정할 경우 자기 자신 + 10cm의 유저가 조회되어야하지만 자기 자신만 조회됨.
		 * 5cm: 아무도 조회 x
		 * 10cm: 아무도 조회 x
		 * 15cm: 자기 자신 조회
		 * 20cm: 자기 자신, 10cm 근처 유저 조회
		 */
		@Test
		void 반경_15cm_내_사용자_검색_오차범위의_한계_확인() {
			// when
			List<NearbyUserDto> nearbyUsers = redisGeoSpatialService.findNearbyUsers(
				BASE_LONGITUDE, BASE_LATITUDE, 0.15, TEST_SESSION_ID);

			// then
			assertThat(nearbyUsers).hasSize(1);

			assertThat(nearbyUsers.get(0).userId()).isEqualTo(CENTER_USER);
		}
	}

	@Nested
	class 정밀도_오차범위_테스트 {

		@BeforeEach
		void setUpPrecisionTestData() {
			redisGeoSpatialService.registerUserLocation(
				CENTER_USER, BASE_LONGITUDE, BASE_LATITUDE, TEST_SESSION_ID);

			for (int distance = 1; distance <= 10; distance++) {
				String userId = "user_" + distance + "m";
				double longitude = BASE_LONGITUDE + (distance * METER_TO_LONGITUDE_DEGREE);
				double latitude = BASE_LATITUDE;
				redisGeoSpatialService.registerUserLocation(userId, longitude, latitude, TEST_SESSION_ID);
			}
		}

		@AfterEach
		void cleanUpPrecisionTestData() {
			redisGeoSpatialService.removeUserLocation(CENTER_USER, TEST_SESSION_ID);

			for (int distance = 1; distance <= 10; distance++) {
				String userId = "user_" + distance + "m";
				redisGeoSpatialService.removeUserLocation(userId, TEST_SESSION_ID);
			}
		}

		@Test
		void 거리_정확도_테스트() {
			for (int expectedDistance = 1; expectedDistance <= 10; expectedDistance++) {
				// given
				String userId = "user_" + expectedDistance + "m";
				double actualDistance = redisGeoSpatialService.calculateDistance(
					CENTER_USER, userId, TEST_SESSION_ID);

				// when
				double lowerBound = expectedDistance * 0.7;
				double upperBound = expectedDistance * 1.3;

				// then
				assertThat(actualDistance)
					.as("거리 %dm의 사용자 거리 계산 검증", expectedDistance)
					.isBetween(lowerBound, upperBound);

				// 결과 view
				System.out.printf("예상 거리 %dm, 실제 측정 거리: %.2fm (오차: %.2f%%)\n",
					expectedDistance, actualDistance,
					((actualDistance - expectedDistance) / expectedDistance) * 100);
			}
		}

		@Test
		void 반경_3m_검색_테스트() {
			// when
			List<NearbyUserDto> nearbyUsers = redisGeoSpatialService.findNearbyUsers(
				BASE_LONGITUDE, BASE_LATITUDE, 3.0, TEST_SESSION_ID);

			// then
			// 3m 이내 사용자들만 조회되어야 함 (center + 1m, 2m, 3m = 총 4명)
			assertThat(nearbyUsers).hasSize(4);

			assertThat(nearbyUsers.get(0).userId()).isEqualTo(CENTER_USER);
			assertThat(nearbyUsers.get(1).userId()).isEqualTo("user_1m");
			assertThat(nearbyUsers.get(2).userId()).isEqualTo("user_2m");
			assertThat(nearbyUsers.get(3).userId()).isEqualTo("user_3m");

			assertThat(nearbyUsers.get(0).distance()).isLessThan(0.2);  // 중심점
			assertThat(nearbyUsers.get(1).distance()).isBetween(0.7, 1.5);    // 1m
			assertThat(nearbyUsers.get(2).distance()).isBetween(1.5, 2.6);    // 2m
			assertThat(nearbyUsers.get(3).distance()).isBetween(2.5, 3.8);    // 3m
		}

		/**
		 * 로그 결과 반영: 7m 이내에는 center + 1m~6m 사용자만 조회됨 (총 7명)
		 * 7m 사용자는 실제 측정값이 7.10m로 경계를 약간 넘어 제외됨
		 */
		@Test
		void 반경_7m_검색_테스트() {
			// when
			List<NearbyUserDto> nearbyUsers = redisGeoSpatialService.findNearbyUsers(
				BASE_LONGITUDE, BASE_LATITUDE, 7.0, TEST_SESSION_ID);

			// then
			assertThat(nearbyUsers).hasSize(7);

			assertThat(nearbyUsers).extracting(NearbyUserDto::userId)
				.containsExactly(CENTER_USER, "user_1m", "user_2m", "user_3m", "user_4m", "user_5m", "user_6m");

			for (int i = 1; i < nearbyUsers.size(); i++) {
				assertThat(nearbyUsers.get(i).distance())
					.isGreaterThanOrEqualTo(nearbyUsers.get(i - 1).distance());
			}

			assertThat(nearbyUsers.get(1).distance()).isCloseTo(1.01, within(0.1));
			assertThat(nearbyUsers.get(2).distance()).isCloseTo(1.96, within(0.1));
			assertThat(nearbyUsers.get(3).distance()).isCloseTo(2.90, within(0.1));
			assertThat(nearbyUsers.get(4).distance()).isCloseTo(3.85, within(0.1));
			assertThat(nearbyUsers.get(5).distance()).isCloseTo(4.80, within(0.1));
			assertThat(nearbyUsers.get(6).distance()).isCloseTo(5.74, within(0.1));

			assertThat(nearbyUsers).extracting(NearbyUserDto::userId)
				.doesNotContain("user_7m", "user_8m", "user_9m", "user_10m");
		}

		@Test
		void 소수점_반경_검색_정확도_테스트() {
			// 2.5m 반경 검색 - 이론상 center + 1m, 2m이 조회되고 3m는 경계에 있을 수 있음
			List<NearbyUserDto> users2_5m = redisGeoSpatialService.findNearbyUsers(
				BASE_LONGITUDE, BASE_LATITUDE, 2.5, TEST_SESSION_ID);

			// 최소 3명(center, 1m, 2m)은 포함되어야 함
			assertThat(users2_5m).hasSizeGreaterThanOrEqualTo(3);
			assertThat(users2_5m).extracting(NearbyUserDto::userId)
				.contains(CENTER_USER, "user_1m", "user_2m");

			// 4m 이상은 절대 포함되면 안 됨
			assertThat(users2_5m).extracting(NearbyUserDto::userId)
				.doesNotContain("user_4m", "user_5m", "user_6m", "user_7m", "user_8m", "user_9m", "user_10m");

			// 5.5m 반경 검색 - 이론상 center + 1m ~ 5m이 조회되고 6m는 경계에 있을 수 있음
			List<NearbyUserDto> users5_5m = redisGeoSpatialService.findNearbyUsers(
				BASE_LONGITUDE, BASE_LATITUDE, 5.5, TEST_SESSION_ID);

			// 최소 6명(center, 1m ~ 5m)은 포함되어야 함
			assertThat(users5_5m).hasSizeGreaterThanOrEqualTo(6);
			assertThat(users5_5m).extracting(NearbyUserDto::userId)
				.contains(CENTER_USER, "user_1m", "user_2m", "user_3m", "user_4m", "user_5m");

			// 7m 이상은 절대 포함되면 안 됨
			assertThat(users5_5m).extracting(NearbyUserDto::userId)
				.doesNotContain("user_7m", "user_8m", "user_9m", "user_10m");
		}
	}

	// @Nested
	// @DisplayName("TTL 및 세션 관리 테스트")
	// class TTLTest {
	//
	// 	@Test
	// 	@DisplayName("세션 위치 데이터 자동 만료 테스트")
	// 	void sessionDataExpirationTest() throws InterruptedException {
	// 		// given
	// 		String tempSessionId = "temp-session-ttl";
	//
	// 		redisGeoSpatialService.registerUserLocation(
	// 			CENTER_USER, BASE_LONGITUDE, BASE_LATITUDE, tempSessionId);
	//
	// 		// when
	// 		boolean expired = redisGeoSpatialService.expireSessionData(tempSessionId, Duration.ofSeconds(1));
	//
	// 		// then
	// 		assertThat(expired).isTrue();
	//
	// 		// 위치 확인 (만료 전)
	// 		Point position = redisGeoSpatialService.getUserPosition(CENTER_USER, tempSessionId);
	// 		assertThat(position).isNotNull();
	//
	// 		// 1초 이상 대기 후 만료 확인
	// 		Thread.sleep(1500);
	//
	// 		// 위치 재확인 (만료 후)
	// 		Point positionAfterExpire = redisGeoSpatialService.getUserPosition(CENTER_USER, tempSessionId);
	// 		// 만료되었으므로 null이어야 함
	// 		assertThat(positionAfterExpire).isNull();
	//
	// 		// 주변 사용자 검색 - 결과가 없어야 함
	// 		List<NearbyUserDto> nearbyUsers = redisGeoSpatialService.findNearbyUsers(
	// 			BASE_LONGITUDE, BASE_LATITUDE, 100.0, tempSessionId);
	// 		assertThat(nearbyUsers).isEmpty();
	// 	}
	// }
}
