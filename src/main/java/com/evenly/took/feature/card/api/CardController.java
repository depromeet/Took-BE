package com.evenly.took.feature.card.api;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.LinkType;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.request.CreateCardRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.meta.LoginUser;
import com.evenly.took.global.response.SuccessResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {

	private final CardService cardService;

	@GetMapping("/api/card/register")
	public SuccessResponse<CareersResponse> getCareers(@RequestParam Job job) {
		CareersResponse response = cardService.findCareers(job);
		return SuccessResponse.of(response);
	}

	@GetMapping("/api/card/my")
	public SuccessResponse<MyCardListResponse> getMyCards() {
		return SuccessResponse.of(new MyCardListResponse(null));
	}

	@GetMapping("/api/card/detail")
	public SuccessResponse<CardDetailResponse> getCardDetail(@RequestParam CardDetailRequest request) {
		return SuccessResponse.of(
			new CardDetailResponse(
				null, null, null, null, null, null,
				null, null, null, null, null, null));
	}

	@PostMapping("/api/card/scrap")
	public SuccessResponse<ScrapResponse> scrapLink(@RequestParam LinkType type, @RequestBody LinkRequest request) {
		return SuccessResponse.of(
			new ScrapResponse(LinkType.BLOG, "title", "link", "image_url", "description"));
	}

	@PostMapping(value = "/api/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public SuccessResponse<Void> addCard(
		@LoginUser User user,
		@ModelAttribute CreateCardRequest request,
		@RequestPart("profileImage") MultipartFile profileImage) {

		// MultiPart 형식의 데이터의 경우, 유효성 검증이 RequestDTO 역직렬화 시, 체크가 안되는 문제가 있어 이후 개별적으로 진행
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<CreateCardRequest>> violations = validator.validate(request);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException("요청 필드 유효성 검사 실패", violations);
		}

		String profileImageKey = this.cardService.uploadProfileImage(profileImage);
		this.cardService.createCard(user, request, profileImageKey);
		return SuccessResponse.created("명함 생성 성공");
	}
}
