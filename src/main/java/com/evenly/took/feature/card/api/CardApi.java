package com.evenly.took.feature.card.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.multipart.MultipartFile;

import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.dto.request.AddCardRequest;
import com.evenly.took.feature.card.dto.request.AddFolderRequest;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.request.FixFolderRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.request.RemoveFolderRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.dto.response.FoldersResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.dto.ErrorResponse;
import com.evenly.took.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
	SuccessResponse<CareersResponse> getCareers(Job job);

	@Operation(
		summary = "내 명함 목록 조회",
		description = "현재 로그인한 사용자가 소유한 모든 명함 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "명함 목록 조회 성공 [null 값은 전달x]")
	})
	SuccessResponse<MyCardListResponse> getMyCards(User user);

	@Operation(
		summary = "명함 상세 정보 조회",
		description = "특정 명함의 모든 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "명함 상세 정보 조회 성공")
	})
	SuccessResponse<CardDetailResponse> getCardDetail(User user, @ParameterObject CardDetailRequest request);

	@Operation(
		summary = "외부 콘텐츠 링크 스크랩",
		description = "블로그, 프로젝트 링크를 스크랩하여 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "링크 스크랩 성공")
	})
	SuccessResponse<ScrapResponse> scrapLink(LinkRequest request);

	@Operation(
		summary = "명함 생성",
		description = "사용자가 입력한 정보를 바탕으로 새로운 명함을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "명함 생성 성공")
	})
	SuccessResponse<Void> addCard(
		User user,
		AddCardRequest request,
		@Parameter(hidden = true)
		MultipartFile profileImage
	);

	@Operation(
		summary = "폴더 생성",
		description = "새로운 폴더를 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "폴더 생성 성공")
	})
	SuccessResponse<Void> addFolder(
		User user,
		AddFolderRequest request
	);

	@Operation(
		summary = "폴더 목록 조회",
		description = "모든 폴더 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "폴더 목록 조회")
	})
	SuccessResponse<FoldersResponse> getFolders(
		User user
	);

	@Operation(
		summary = "폴더 이름 변경",
		description = "폴더 이름을 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "폴더 이름 변경 성공"),
		@ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "폴더에 대한 권한 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "400", description = "이미 삭제된 폴더",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	SuccessResponse<Void> fixFolder(
		User user,
		FixFolderRequest request
	);

	@Operation(
		summary = "폴더 제거",
		description = "명함 폴더를 제거합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "폴더 제거 성공"),
		@ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "폴더에 대한 권한 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "400", description = "이미 삭제된 폴더",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	SuccessResponse<Void> removeFolder(
		User user,
		RemoveFolderRequest request
	);
}
