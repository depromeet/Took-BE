package com.evenly.took.global.redis;

import java.util.List;

import org.springframework.data.geo.Point;

import com.evenly.took.global.redis.dto.NearbyUserDto;

public interface RedisGeoSpatialService {

	boolean registerUserLocation(String userId, double longitude, double latitude);

	double calculateDistance(String userId1, String userId2);

	List<NearbyUserDto> findNearbyUsers(double longitude, double latitude, double radiusMeters);

	Point getUserPosition(String userId);

	boolean removeUserLocation(String userId);
}
