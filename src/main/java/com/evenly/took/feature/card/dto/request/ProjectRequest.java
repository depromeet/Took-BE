package com.evenly.took.feature.card.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 정보")
public record ProjectRequest(
	@Schema(description = "플랫폼 종류 (작성한 글, 대표 프로젝트)", example = "project")
	String type,

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
