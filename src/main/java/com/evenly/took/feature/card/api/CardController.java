package com.evenly.took.feature.card.api;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.evenly.took.feature.card.dto.request.FixFolderRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.request.RemoveFolderRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.dto.response.FoldersResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.meta.LoginUser;
import com.evenly.took.global.response.SuccessResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {

	private final CardService cardService;

	@GetMapping("/api/card/my")
	public SuccessResponse<MyCardListResponse> getMyCards(@LoginUser User user) {
		MyCardListResponse response = cardService.findUserCardList(user.getId());
		return SuccessResponse.of(response);
	}

	@GetMapping("/api/card/detail")
	public SuccessResponse<CardDetailResponse> getCardDetail(@LoginUser User user,
		@ModelAttribute @Valid CardDetailRequest request) {
		CardDetailResponse response = cardService.findCardDetail(user.getId(), request);
		return SuccessResponse.of(response);
	}

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

}
