package com.evenly.took.global.redis.dto;

public record NearbyUserDto(
	String userId,
	double longitude,
	double latitude,
	double distance
) {
}
