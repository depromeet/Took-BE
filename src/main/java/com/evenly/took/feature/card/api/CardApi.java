package com.evenly.took.feature.card.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[2. Card]")
public interface CardApi {

	@Operation(
		summary = "명함 생성용 직군 목록 조회",
		description = "명함 생성 시 선택 가능한 직군 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "직군 목록 조회 성공")
	})
	@GetMapping("/api/card/register")
	void getJobCategories();

	@Operation(
		summary = "내 명함 목록 조회",
		description = "현재 로그인한 사용자가 소유한 모든 명함 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "명함 목록 조회 성공")
	})
	@GetMapping("/api/card/my")
	void getMyCards();

	@Operation(
		summary = "명함 상세 정보 조회",
		description = "특정 명함의 모든 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "명함 상세 정보 조회 성공")
	})
	@GetMapping("/api/card/detail")
	void getCardDetail();

	@Operation(
		summary = "외부 콘텐츠 링크 스크랩",
		description = "블로그, SNS, 프로젝트 등의 외부 링크를 명함에 스크랩합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "콘텐츠 스크랩 성공")
	})
	@PostMapping("/api/card/scrap")
	void scrapExternalContent();

	@Operation(
		summary = "명함 생성",
		description = "사용자가 입력한 정보를 바탕으로 새로운 명함을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "명함 생성 성공")
	})
	@PostMapping("/api/card")
	void createCard();
}
