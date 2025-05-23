package com.evenly.took.feature.card.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "링크 스크랩 응답")
public record ScrapResponse(
	@Schema(description = "제목", example = "Took-BE")
	String title,

	@Schema(description = "링크", example = "https://github.com/depromeet/Took-BE")
	String link,

	@Schema(description = "대표 이미지", example = "https://opengraph.githubassets.com/c160604aeafcebc6a109147d64981a898a9c28514759b545624306b9f1ffe4bf/depromeet/Took-BE")
	String imageUrl,

	@Schema(description = "설명", example = "2븐하게 Server 레포입니다.")
	String description
) {
}
