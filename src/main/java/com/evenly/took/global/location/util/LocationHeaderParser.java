package com.evenly.took.global.location.util;

import org.springframework.data.geo.Point;

import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.location.LocationErrorCode;

import jakarta.servlet.http.HttpServletRequest;

public class LocationHeaderParser {

	private static final String PARSE_REGEX = ",";
	private static final String X_REDIS_GEO = "x-redis-geo";

	public static Point parse(String header) {
		String[] parts = header.split(PARSE_REGEX);
		if (parts.length != 2) {
			throw new TookException(LocationErrorCode.INVALID_PARSE_LOCATION);
		}
		double latitude = Double.parseDouble(parts[0].trim());
		double longitude = Double.parseDouble(parts[1].trim());
		return new Point(longitude, latitude);
	}

	public static Point extractPoint(HttpServletRequest request) {
		String geoHeader = request.getHeader(X_REDIS_GEO);
		if (geoHeader == null || geoHeader.isBlank()) {
			return null;
		}
		return parse(geoHeader);
	}
}
