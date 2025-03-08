package com.evenly.took.feature.card.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "명함을 추가하기 위한 정보", requiredMode = Schema.RequiredMode.REQUIRED)
public record CreateCardRequest(
	@Schema(description = "사용자 프로필 이미지", requiredMode = Schema.RequiredMode.REQUIRED)
	MultipartFile profileImage,

	@Schema(description = "이름", example = "윤장원", requiredMode = Schema.RequiredMode.REQUIRED)
	String nickname,

	@Schema(description = "세부직군 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long detailJobId,

	@Schema(description = "관심 도메인", example = "[\"웹\", \"모바일\", \"클라우드\"]", requiredMode = Schema.RequiredMode.REQUIRED)
	List<String> interestDomain,

	@Schema(description = "한 줄 소개", example = "백엔드 개발을 좋아하는 개발자입니다", requiredMode = Schema.RequiredMode.REQUIRED)
	String summary,

	@Schema(description = "소속 정보", example = "ABC 회사", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	String organization,

	@Schema(description = "SNS 정보", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	List<SNSRequest> sns,

	@Schema(description = "활동 지역", example = "서울 강남구", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	String region,

	@Schema(description = "취미 정보", example = "등산, 독서", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	String hobby,

	@Schema(description = "최근 소식", example = "최근 블로그 포스팅 시작했습니다", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	String news,

	@Schema(description = "작성한 글 정보", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	List<ContentRequest> content,

	@Schema(description = "프로젝트 정보", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	List<ProjectRequest> project
) {
}
