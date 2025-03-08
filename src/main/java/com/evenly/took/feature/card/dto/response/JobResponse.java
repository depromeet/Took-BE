package com.evenly.took.feature.card.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "명함 직군 조회 응답")
public record JobResponse(
	@Schema(description = "직군 (디자이너, 개발자)", example = "DEVELOPER")
	String job,

	@Schema(description = "세부 직군 영어", example = "Server Developer")
	String detailJob,

	@Schema(description = "세부 직군 한글", example = "[\"백엔드 개발자\", \"서버 개발자\"]")
	List<String> detailJobDescriptions
) {
}
