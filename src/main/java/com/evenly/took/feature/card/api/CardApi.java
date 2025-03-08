package com.evenly.took.feature.card.api;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.LinkType;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.request.CreateCardRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.JobsResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[2. Card]")
public interface CardApi {

	@Operation(
		summary = "명함 직군 목록 조회",
		description = "명함 생성 시 선택 가능한 직군 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "직군 목록 조회 성공")
	})
	SuccessResponse<JobsResponse> getJobs(
		@RequestParam(value = "job") Job job);

	@Operation(
		summary = "내 명함 목록 조회",
		description = "현재 로그인한 사용자가 소유한 모든 명함 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "명함 목록 조회 성공 [null 값은 전달x]")
	})
	SuccessResponse<MyCardListResponse> getMyCards();

	@Operation(
		summary = "명함 상세 정보 조회",
		description = "특정 명함의 모든 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "명함 상세 정보 조회 성공")
	})
	SuccessResponse<CardDetailResponse> getCardDetail(
		@ModelAttribute CardDetailRequest request
	);

	@Operation(
		summary = "외부 콘텐츠 링크 스크랩",
		description = "블로그, 프로젝트 링크를 스크랩하여 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "링크 스크랩 성공")
	})
	SuccessResponse<ScrapResponse> scrapLink(
		@RequestParam LinkType type,
		@RequestBody LinkRequest request);

	@Operation(
		summary = "명함 생성",
		description = "사용자가 입력한 정보를 바탕으로 새로운 명함을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "명함 생성 성공")
	})
	SuccessResponse<Void> createCard(
		@RequestBody CreateCardRequest request

	);
}
