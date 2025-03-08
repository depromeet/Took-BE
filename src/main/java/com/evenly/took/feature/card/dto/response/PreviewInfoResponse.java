package com.evenly.took.feature.card.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "썸네일 명함에 대표로 보여줄 정보 1가지")
public record PreviewInfoResponse(
	@Schema(description = "프로젝트 정보 (previewInfoType이 PROJECT일 때)")
	ProjectResponse project,

	@Schema(description = "작성한 글 정보 (previewInfoType이 CONTENT일 때)")
	ContentResponse content,

	@Schema(description = "취미 정보 (previewInfoType이 HOBBY일 때)", example = "등산, 독서")
	String hobby,

	@Schema(description = "SNS 정보 (previewInfoType이 SNS일 때)")
	SNSResponse sns,

	@Schema(description = "최근 소식 (previewInfoType이 NEWS일 때)", example = "최근 블로그 포스팅 시작했습니다")
	String news,

	@Schema(description = "활동 지역 (previewInfoType이 MAIN_REGION일 때)", example = "서울 강남구")
	String region
) {
}
