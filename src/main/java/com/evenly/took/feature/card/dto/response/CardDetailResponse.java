package com.evenly.took.feature.card.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Job;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "명함 상세 정보")
public record CardDetailResponse(
	@Schema(description = "명함용 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
	String nickname,

	@Schema(description = "직군", example = "DEVELOPER")
	Job job,

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
	List<SNSResponse> sns,

	@Schema(description = "최근 소식 (previewInfoType이 NEWS일 때)", example = "최근 블로그 포스팅 시작했습니다")
	String news,

	@Schema(description = "취미 정보 (previewInfoType이 HOBBY일 때)", example = "등산, 독서")
	String hobby,

	@Schema(description = "작성한 글 정보 (previewInfoType이 CONTENT일 때)")
	List<ContentResponse> content,

	@Schema(description = "프로젝트 정보 (previewInfoType이 PROJECT일 때)")
	List<ProjectResponse> project
) {
	public static CardDetailResponse from(Card card) {
		Job job = card.getCareer() != null ? card.getCareer().getJob() : null;
		String detailJob = card.getCareer() != null ? card.getCareer().getDetailJobEn() : null;

		List<ContentResponse> contentResponses = null;
		if (card.getContent() != null && !card.getContent().isEmpty()) {
			contentResponses = card.getContent().stream()
				.map(ContentResponse::from)
				.collect(Collectors.toList());
		}

		List<ProjectResponse> projectResponses = null;
		if (card.getProject() != null && !card.getProject().isEmpty()) {
			projectResponses = card.getProject().stream()
				.map(ProjectResponse::from)
				.collect(Collectors.toList());
		}

		List<SNSResponse> snsResponses = null;
		if (card.getSns() != null && !card.getSns().isEmpty()) {
			snsResponses = card.getSns().stream()
				.map(SNSResponse::from)
				.collect(Collectors.toList());
		}

		return new CardDetailResponse(
			card.getNickname(),
			job,
			detailJob,
			card.getOrganization(),
			card.getSummary(),
			card.getRegion(),
			card.getInterestDomain(),
			snsResponses,
			card.getNews(),
			card.getHobby(),
			contentResponses,
			projectResponses
		);
	}
}