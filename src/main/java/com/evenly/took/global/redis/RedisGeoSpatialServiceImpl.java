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

	private static final String LOCATION_KEY_PREFIX = "user_locations:"; // TODO: 임시 key prefix
	private static final Duration DEFAULT_TTL = Duration.ofHours(2); // TODO: 임시 TTL

	public boolean registerUserLocation(String userId, double longitude, double latitude, String sessionId) {
		try {
			String key = buildLocationKey(sessionId);
			Point userLocation = new Point(longitude, latitude);
			Long locationAddResult = redisTemplate.opsForGeo().add(key, userLocation, userId);

			redisTemplate.expire(key, DEFAULT_TTL);

			return isLocationAddSuccessful(locationAddResult);
		} catch (Exception e) {
			log.error("Redis Geo Add Error :: {}", e.getMessage(), e);
			return false;
		}
	}

	private boolean isLocationAddSuccessful(final Long locationAddResult) {
		return locationAddResult != null && locationAddResult > 0;
	}

	public List<NearbyUserDto> findNearbyUsers(double longitude, double latitude,
		double radiusMeters, String sessionId) {
		try {
			String key = buildLocationKey(sessionId);

			SearchParameters params = createSearchParameters(longitude, latitude, radiusMeters);

			GeoResults<GeoLocation<Object>> results = executeGeoRadiusSearch(key, params);

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

		RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
			.includeDistance()
			.includeCoordinates()
			.sortAscending();

		return new SearchParameters(circle, args);
	}

	private GeoResults<GeoLocation<Object>> executeGeoRadiusSearch(String key, SearchParameters params) {
		return redisTemplate.opsForGeo().radius(key, params.circle(), params.args());
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

	public double calculateDistance(String userId1, String userId2, String sessionId) {
		try {
			String key = buildLocationKey(sessionId);
			Distance distance = redisTemplate.opsForGeo().distance(key, userId1, userId2, DistanceUnit.METERS);
			return distance != null ? distance.getValue() : -1;
		} catch (Exception e) {
			log.error("Redis Geo Distance Error :: {}", e.getMessage(), e);
			return -1;
		}
	}

	public boolean removeUserLocation(String userId, String sessionId) {
		try {
			String key = buildLocationKey(sessionId);
			Long removed = redisTemplate.opsForGeo().remove(key, userId);
			return removed != null && removed > 0;
		} catch (Exception e) {
			log.error("Redis Geo Remove Error :: {}", e.getMessage(), e);
			return false;
		}
	}

	public boolean expireSessionData(String sessionId, Duration ttl) {
		String key = buildLocationKey(sessionId);
		return Boolean.TRUE.equals(redisTemplate.expire(key, ttl));
	}

	public Point getUserPosition(String userId, String sessionId) {
		try {
			String key = buildLocationKey(sessionId);
			List<Point> positions = redisTemplate.opsForGeo().position(key, userId);
			return positions != null && !positions.isEmpty() ? positions.get(0) : null;
		} catch (Exception e) {
			log.error("Redis Geo Position Error :: {}", e.getMessage(), e);
			return null;
		}
	}

	public String getUserGeohash(String userId, String sessionId) {
		try {
			String key = buildLocationKey(sessionId);
			List<String> geohashes = redisTemplate.opsForGeo().hash(key, userId);
			return geohashes != null && !geohashes.isEmpty() ? geohashes.get(0) : null;
		} catch (Exception e) {
			log.error("Redis Geo Hash Error :: {}", e.getMessage(), e);
			return null;
		}
	}

	public List<String> getAllUsersInSession(String sessionId) {
		try {
			String key = buildLocationKey(sessionId);
			Circle circle = new Circle(new Point(0, 0), new Distance(Double.MAX_VALUE, DistanceUnit.KILOMETERS));
			GeoResults<GeoLocation<Object>> results = redisTemplate.opsForGeo().radius(key, circle);

			List<String> userIds = new ArrayList<>();
			if (results != null) {
				for (GeoResult<GeoLocation<Object>> result : results) {
					userIds.add((String)result.getContent().getName());
				}
			}

			return userIds;
		} catch (Exception e) {
			log.error("Redis Get All Users Error :: {}", e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	private String buildLocationKey(String sessionId) {
		return LOCATION_KEY_PREFIX + sessionId;
	}
}
