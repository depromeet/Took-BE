package com.evenly.took.global.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.evenly.took.global.redis.dto.NearbyUserDto;
import com.evenly.took.global.redis.dto.SearchParameters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGeoSpatialServiceImpl implements RedisGeoSpatialService {

	private final RedisTemplate<String, Object> redisTemplate;

	private static final String LOCATION_KEY = "user_locations";
	private static final Duration DEFAULT_TTL = Duration.ofHours(2);

	@Override
	public boolean registerUserLocation(String userId, double longitude, double latitude) {
		try {
			Point userLocation = new Point(longitude, latitude);
			Long locationAddResult = redisTemplate.opsForGeo().add(LOCATION_KEY, userLocation, userId);
			redisTemplate.expire(LOCATION_KEY, DEFAULT_TTL);
			log.info("Redis 위치 등록 시도: userId={}, lon={}, lat={}", userId, longitude, latitude);
			return locationAddResult != null && locationAddResult > 0;
		} catch (Exception e) {
			log.error("Redis Geo Add Error :: {}", e.getMessage(), e);
			return false;
		}
	}

	@Override
	public List<NearbyUserDto> findNearbyUsers(double longitude, double latitude, double radiusMeters) {
		try {
			SearchParameters params = createSearchParameters(longitude, latitude, radiusMeters);
			GeoResults<GeoLocation<Object>> results = redisTemplate.opsForGeo()
				.radius(LOCATION_KEY, params.circle(), params.args());

			if (results == null) {
				return Collections.emptyList();
			}
			return convertToNearbyUserDtoList(results);
		} catch (Exception e) {
			log.error("Redis Geo Radius Error :: {}", e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	private SearchParameters createSearchParameters(double longitude, double latitude, double radiusMeters) {
		Point center = new Point(longitude, latitude);
		Distance radius = new Distance(radiusMeters, DistanceUnit.METERS);
		Circle circle = new Circle(center, radius);

		RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
			.newGeoRadiusArgs()
			.includeDistance()
			.includeCoordinates()
			.sortAscending();

		return new SearchParameters(circle, args);
	}

	private List<NearbyUserDto> convertToNearbyUserDtoList(GeoResults<GeoLocation<Object>> results) {
		List<NearbyUserDto> nearbyUsers = new ArrayList<>();
		for (GeoResult<GeoLocation<Object>> result : results) {
			nearbyUsers.add(convertToNearbyUserDto(result));
		}
		return nearbyUsers;
	}

	private NearbyUserDto convertToNearbyUserDto(GeoResult<GeoLocation<Object>> result) {
		String userId = (String)result.getContent().getName();
		Point point = result.getContent().getPoint();
		double distance = result.getDistance().getValue();
		return new NearbyUserDto(userId, point.getX(), point.getY(), distance);
	}

	@Override
	public double calculateDistance(String userId1, String userId2) {
		try {
			String key = buildLocationKey();
			Distance distance = redisTemplate.opsForGeo().distance(key, userId1, userId2, DistanceUnit.METERS);
			return distance != null ? distance.getValue() : -1;
		} catch (Exception e) {
			log.error("Redis Geo Distance Error :: {}", e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public Point getUserPosition(String userId) {
		try {
			String key = buildLocationKey();
			List<Point> positions = redisTemplate.opsForGeo().position(key, userId);
			return positions != null && !positions.isEmpty() ? positions.get(0) : null;
		} catch (Exception e) {
			log.error("Redis Geo Position Error :: {}", e.getMessage(), e);
			return null;
		}
	}

	@Override
	public boolean removeUserLocation(String userId) {
		try {
			String key = buildLocationKey();
			Long removed = redisTemplate.opsForGeo().remove(key, userId);
			return removed != null && removed > 0;
		} catch (Exception e) {
			log.error("Redis Geo Remove Error :: {}", e.getMessage(), e);
			return false;
		}
	}

	private String buildLocationKey() {
		return LOCATION_KEY;
	}
}
