package com.evenly.took.feature.card.dto.response;

import com.evenly.took.feature.card.domain.vo.Project;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 정보")
public record ProjectResponse(
	@Schema(description = "프로젝트 제목", example = "Took")
	String title,

	@Schema(description = "프로젝트 링크", example = "https://github.com/username/project")
	String link,

	@Schema(description = "프로젝트 대표 이미지 URL", example = "https://avatars.githubusercontent.com/u/18240792?s=48&v=4")
	String imageUrl,

	@Schema(description = "프로젝트 설명", example = "Spring Boot 기반 명함 관리 서비스")
	String description
) {
	public ProjectResponse from(Project project) {
		return new ProjectResponse(project.title(), project.link(), project.imageUrl(), project.description());
	}
}
