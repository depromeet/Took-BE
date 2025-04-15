package com.evenly.took.feature.user.dto.response;

public record NearbyUserCardProfileResponse(
	Long userId,
	Long cardId,
	String nickname,
	String detailJobEn,
	String imagePath
) implements NearbyUserProfileResponse {
}
