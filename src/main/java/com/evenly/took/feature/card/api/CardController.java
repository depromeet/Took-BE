package com.evenly.took.feature.card.api;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.dto.request.AddCardRequest;
import com.evenly.took.feature.card.dto.request.AddFolderRequest;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.request.CardRequest;
import com.evenly.took.feature.card.dto.request.FixCardRequest;
import com.evenly.took.feature.card.dto.request.FixFolderRequest;
import com.evenly.took.feature.card.dto.request.FixReceivedCardRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.request.NewReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.ReceiveCardRequest;
import com.evenly.took.feature.card.dto.request.ReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.RemoveFolderRequest;
import com.evenly.took.feature.card.dto.request.RemoveReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.SendCardRequest;
import com.evenly.took.feature.card.dto.request.SetReceivedCardsFolderRequest;
import com.evenly.took.feature.card.dto.request.SetReceivedCardsMemoRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.CardResponse;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.dto.response.FoldersResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.ReceivedCardListResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.meta.LoginUser;
import com.evenly.took.global.auth.meta.PublicApi;
import com.evenly.took.global.location.meta.RegisterLocation;
import com.evenly.took.global.monitoring.slack.SlackErrorAlert;
import com.evenly.took.global.response.SuccessResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SlackErrorAlert
@RegisterLocation
@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {

	private final CardService cardService;

	@GetMapping("/api/card/my")
	public SuccessResponse<MyCardListResponse> getMyCards(@LoginUser User user) {
		MyCardListResponse response = cardService.findUserCardList(user.getId());
		return SuccessResponse.of(response);
	}

	@DeleteMapping("/api/card/{cardId}")
	public SuccessResponse<Void> deleteMyCards(@LoginUser User user, @PathVariable Long cardId) {
		cardService.softDeleteMyCard(user.getId(), cardId);
		return SuccessResponse.deleted("내 명함 삭제 성공");
	}

	@GetMapping("/api/card/detail")
	public SuccessResponse<CardDetailResponse> getCardDetail(@LoginUser User user,
		@ModelAttribute @Valid CardDetailRequest request) {
		CardDetailResponse response = cardService.findCardDetail(user.getId(), request);
		return SuccessResponse.of(response);
	}

	@PublicApi
	@GetMapping("/api/card/register")
	public SuccessResponse<CareersResponse> getCareers(@RequestParam Job job) {
		CareersResponse response = cardService.findCareers(job);
		return SuccessResponse.of(response);
	}

	@PostMapping("/api/card/scrap")
	public SuccessResponse<ScrapResponse> scrapLink(@RequestBody @Valid LinkRequest request) {
		ScrapResponse response = cardService.scrapLink(request);
		return SuccessResponse.of(response);
	}

	@PostMapping(value = "/api/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public SuccessResponse<Void> addCard(
		@LoginUser User user,
		@ModelAttribute AddCardRequest request,
		@RequestPart("profileImage") MultipartFile profileImage) {

		// MultiPart 형식의 데이터의 경우, 유효성 검증이 RequestDTO 역직렬화 시, 체크가 안되는 문제가 있어 이후 개별적으로 진행
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<AddCardRequest>> violations = validator.validate(request);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException("요청 필드 유효성 검사 실패", violations);
		}

		String profileImageKey = cardService.uploadProfileImage(profileImage);
		cardService.createCard(user, request, profileImageKey);
		return SuccessResponse.created("명함 생성 성공");
	}

	@PostMapping("/api/card/folder")
	public SuccessResponse<Void> addFolder(@LoginUser User user, @RequestBody @Valid AddFolderRequest request) {
		cardService.createFolder(user, request);
		return SuccessResponse.created("폴더 생성 성공");
	}

	@GetMapping("/api/card/folders")
	public SuccessResponse<FoldersResponse> getFolders(@LoginUser User user) {
		FoldersResponse response = cardService.findFolders(user);
		return SuccessResponse.of(response);
	}

	@PutMapping("/api/card/folder")
	public SuccessResponse<Void> fixFolder(@LoginUser User user, @RequestBody @Valid FixFolderRequest request) {
		cardService.updateFolder(user, request);
		return SuccessResponse.ok("폴더 이름 변경 성공");
	}

	@DeleteMapping("/api/card/folder")
	public SuccessResponse<Void> removeFolder(@LoginUser User user, @RequestBody @Valid RemoveFolderRequest request) {
		cardService.deleteFolder(user, request);
		return SuccessResponse.ok("폴더 제거 성공");
	}

	@PublicApi
	@GetMapping("/api/card/open")
	public SuccessResponse<CardResponse> getCardOpen(
		@ModelAttribute @Valid CardRequest request) {
		CardResponse response = cardService.findCardOpen(request);
		return SuccessResponse.of(response);
	}

	@PublicApi
	@GetMapping("/api/card/open/detail")
	public SuccessResponse<CardDetailResponse> getCardDetailOpen(
		@ModelAttribute @Valid CardDetailRequest request) {
		CardDetailResponse response = cardService.findCardDetailOpen(request);
		return SuccessResponse.of(response);
	}

	@PostMapping("/api/card/receive")
	public SuccessResponse<Void> receiveCard(
		@LoginUser User user,
		@RequestBody @Valid ReceiveCardRequest request
	) {
		cardService.receiveCard(user, request);
		return SuccessResponse.created("명함 수신 성공");
	}

	@PutMapping("/api/card/receive/folder")
	public SuccessResponse<Void> setReceivedCardsFolder(
		@LoginUser User user,
		@RequestBody @Valid SetReceivedCardsFolderRequest request
	) {
		cardService.setReceivedCardsFolder(user, request);
		return SuccessResponse.ok("명함 폴더 설정 성공");
	}

	@GetMapping("/api/card/receive")
	public SuccessResponse<ReceivedCardListResponse> getReceivedCards(
		@LoginUser User user,
		@ModelAttribute ReceivedCardsRequest request
	) {
		ReceivedCardListResponse response = cardService.findReceivedCards(user, request);
		return SuccessResponse.of(response);
	}
	
	/**
	 * 흥미로운 명함 목록 조회 (내 대표명함의 관심사와 하나라도 겹치는 명함)
	 * 새로 추가된 받은 명함 중, 내 대표명함의 관심사와 하나라도 겹치는 명함들을 반환합니다.
	 */
	@GetMapping("/api/card/receive/interesting")
	public SuccessResponse<ReceivedCardListResponse> getInterestingNewReceivedCards(
		@LoginUser User user,
		@ModelAttribute NewReceivedCardsRequest request
	) {
		ReceivedCardListResponse response = cardService.findInterestingNewReceivedCards(user, request);
		return SuccessResponse.of(response);
	}
	
	/**
	 * 한줄 메모 명함 목록 조회 (관심사 불일치 + 메모 없음)
	 * 새로 추가된 받은 명함 중, 내 대표명함의 관심사와 겹치지 않고 메모가 없는 명함들을 반환합니다.
	 */
	@GetMapping("/api/card/receive/memo")
	public SuccessResponse<ReceivedCardListResponse> getMemoNeededNewReceivedCards(
		@LoginUser User user,
		@ModelAttribute NewReceivedCardsRequest request
	) {
		ReceivedCardListResponse response = cardService.findMemoNeededNewReceivedCards(user, request);
		return SuccessResponse.of(response);
	}

	@DeleteMapping("/api/card/receive")
	public SuccessResponse<Void> removeReceivedCards(
		@LoginUser User user,
		@RequestBody @Valid RemoveReceivedCardsRequest request
	) {
		cardService.removeReceivedCards(user, request);
		return SuccessResponse.ok("명함 삭제 성공");
	}

	@PutMapping("/api/card/receive")
	public SuccessResponse<Void> fixReceivedCard(
		@LoginUser User user,
		@RequestBody @Valid FixReceivedCardRequest request
	) {
		cardService.updateReceivedCard(user, request);
		return SuccessResponse.ok("명함 업데이트 성공");
	}
	
	@PutMapping("/api/card/receive/memo/batch")
	public SuccessResponse<Void> setReceivedCardsMemo(
		@LoginUser User user,
		@RequestBody @Valid SetReceivedCardsMemoRequest request
	) {
		cardService.updateReceivedCardsMemo(user, request.cardMemos());
		return SuccessResponse.ok("한줄 메모 추가 성공");
	}

	@PutMapping(value = "/api/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public SuccessResponse<Void> fixCard(
		@LoginUser User user,
		@ModelAttribute FixCardRequest request,
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<FixCardRequest>> violations = validator.validate(request);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException("요청 필드 유효성 검사 실패", violations);
		}

		cardService.updateCard(user, request, profileImage);
		return SuccessResponse.ok("내 명함 수정 성공");
	}

	@PostMapping("/api/card/{id}/primary")
	public SuccessResponse<Void> setPrimaryCard(
		@LoginUser User user,
		@PathVariable("id") Long cardId
	) {
		cardService.setPrimaryCard(user.getId(), cardId);
		return SuccessResponse.ok("대표 명함 설정 성공");
	}

	@PostMapping("/api/card/send")
	public SuccessResponse<Void> sendCard(
		@LoginUser User user,
		@RequestBody @Valid SendCardRequest request
	) {
		cardService.sendCardToUser(user.getId(), request.targetUserId(), request.cardId());
		return SuccessResponse.created("명함 발신 성공");
	}
}
