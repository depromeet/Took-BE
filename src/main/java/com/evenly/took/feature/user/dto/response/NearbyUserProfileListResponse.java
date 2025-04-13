package com.evenly.took.feature.user.dto.response;

import java.util.List;

public record NearbyUserProfileListResponse(
	List<NearbyUserProfileResponse> profiles
) {
}
