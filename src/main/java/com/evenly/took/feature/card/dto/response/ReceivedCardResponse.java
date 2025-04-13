package com.evenly.took.feature.card.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReceivedCardResponse(
	@Schema(description = "명함 ID", example = "1")
	Long id,

	@Schema(description = "명함 받은 시간", example = "2025-03-14 03:03:04.374273")
	LocalDateTime receivedAt,

	@Schema(description = "명함용 이름", example = "홍길동")
	String nickname,

	@Schema(description = "소속 정보 (nullable)", example = "ABC 회사")
	String organization,

	@Schema(description = "직군", example = "DEVELOPER")
	Job job,

	@Schema(description = "세부 직군", example = "백엔드 개발자")
	String detailJob,

	@Schema(description = "한줄 소개", example = "백엔드 개발을 좋아하는 개발자입니다")
	String summary,

	@Schema(description = "관심 도메인", example = "[\"웹\", \"모바일\", \"클라우드\"]")
	List<String> interestDomain,

	@Schema(description = "썸네일 대표 정보 타입", example = "PROJECT")
	PreviewInfoType previewInfoType,

	@Schema(description = "대표 정보에 따른 상세 내용")
	PreviewInfoResponse previewInfo,

	@Schema(description = "프로필 사진 url", example = "https://s3/images/profile/user1.jpg")
	String imagePath
) {
}
