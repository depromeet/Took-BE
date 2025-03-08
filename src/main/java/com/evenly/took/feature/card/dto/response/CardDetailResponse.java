package com.evenly.took.feature.card.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CardDetailResponse(
	@Schema(description = "명함용 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
	String nickname,

	@Schema(description = "세부 직군 영어", example = "Frontend Develop", requiredMode = Schema.RequiredMode.REQUIRED)
	String detailJob,

	@Schema(description = "소속 정보", example = "ABC 회사")
	String organization,

	@Schema(description = "한줄 소개", example = "문제를 개발으로 해결하고, 세상을 좀 더 멋지게 바꾸고 싶어요. ")
	String summary,

	@Schema(description = "활동 지역 (previewInfoType이 MAIN_REGION일 때)", example = "서울 강남구")
	String region,

	@Schema(description = "관심 도메인", example = "[\"웹\", \"모바일\", \"클라우드\"]")
	List<String> interestDomain,

	@Schema(description = "SNS 정보 (previewInfoType이 SNS일 때)")
	SNSResponse sns,

	@Schema(description = "최근 소식 (previewInfoType이 NEWS일 때)", example = "최근 블로그 포스팅 시작했습니다")
	String news,

	@Schema(description = "취미 정보 (previewInfoType이 HOBBY일 때)", example = "등산, 독서")
	String hobby,

	@Schema(description = "작성한 글 정보 (previewInfoType이 CONTENT일 때)")
	List<ContentResponse> content,

	@Schema(description = "프로젝트 정보 (previewInfoType이 PROJECT일 때)")
	List<ProjectResponse> project
) {
}
