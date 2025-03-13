package com.evenly.took.feature.card.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.card.domain.Job;
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

import jakarta.validation.Valid;
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
	public SuccessResponse<MyCardListResponse> getMyCards(@LoginUser User user) {
		MyCardListResponse response = cardService.findUserCardList(user.getId());
		return SuccessResponse.of(response);
	}

	@GetMapping("/api/card/detail")
	public SuccessResponse<CardDetailResponse> getCardDetail(@LoginUser User user,
		@ModelAttribute @Valid CardDetailRequest request) {
		System.out.println(user.getId());
		CardDetailResponse response = cardService.findCardDetail(user.getId(), request);
		return SuccessResponse.of(response);
	}

	@PostMapping("/api/card/scrap")
	public SuccessResponse<ScrapResponse> scrapLink(@RequestBody @Valid LinkRequest request) {
		ScrapResponse response = cardService.scrapLink(request);
		return SuccessResponse.of(response);
	}

	@PostMapping(value = "/api/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public SuccessResponse<Void> createCard(CreateCardRequest request,
		@RequestParam("profileImage") MultipartFile profileImage) {

		return SuccessResponse.created("명함 생성 성공");
	}
}
