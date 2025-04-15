package com.evenly.took.feature.user.dto.response;

public record NearbyUserBasicProfileResponse(
	Long userId,
	String name
) implements NearbyUserProfileResponse {
}
