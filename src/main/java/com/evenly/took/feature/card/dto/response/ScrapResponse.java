package com.evenly.took.feature.card.dto.response;

import com.evenly.took.feature.card.domain.LinkType;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "링크 스크랩 응답")
public record ScrapResponse(
	@Schema(description = "스크랩 대상 (BLOG, PROJECT)", example = "PROJECT")
	LinkType type,

	@Schema(description = "제목", example = "Took-BE")
	String title,

	@Schema(description = "링크", example = "https://github.com/depromeet/Took-BE")
	String link,

	@Schema(description = "대표 이미지", example = "https://opengraph.githubassets.com/c160604aeafcebc6a109147d64981a898a9c28514759b545624306b9f1ffe4bf/depromeet/Took-BE")
	String imageUrl,

	@Schema(description = "설명", example = "2븐하게 Server 레포입니다.")
	String description
) {

	public static ScrapResponse toResponse(Content content) {
		return new ScrapResponse(
			LinkType.BLOG, content.title(), content.link(), content.imageUrl(), content.description());
	}

	public static ScrapResponse toResponse(Project project) {
		return new ScrapResponse(
			LinkType.PROJECT, project.title(), project.link(), project.imageUrl(), project.description());
	}
}
