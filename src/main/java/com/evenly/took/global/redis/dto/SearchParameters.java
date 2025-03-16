package com.evenly.took.global.redis.dto;

import org.springframework.data.geo.Circle;
import org.springframework.data.redis.connection.RedisGeoCommands;

public record SearchParameters(
	Circle circle,
	RedisGeoCommands.GeoRadiusCommandArgs args
) {
}
