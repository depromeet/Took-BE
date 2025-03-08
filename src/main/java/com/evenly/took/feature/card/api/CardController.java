package com.evenly.took.feature.card.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.card.domain.CardJob;
import com.evenly.took.feature.card.domain.LinkType;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.JobResponse;
import com.evenly.took.feature.card.dto.response.JobsResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {

	@GetMapping("/api/card/register")
	public SuccessResponse<JobsResponse> getJobs(@RequestParam CardJob job) {
		return SuccessResponse.of(
			new JobsResponse(List.of(
				new JobResponse(1L, "designer", "Product Designer", List.of("프로덕트 디자이너")),
				new JobResponse(2L, "designer", "Graphic Designer", List.of("그래픽 디자이너")),
				new JobResponse(3L, "designer", "Interaction Designer", List.of("인터랙션 디자이너", "인터렉션 디자이너")))));
	}

	@GetMapping("/api/card/my")
	public SuccessResponse<MyCardListResponse> getMyCards() {
		return SuccessResponse.of(new MyCardListResponse(null));
	}

	@GetMapping("/api/card/detail")
	public SuccessResponse<CardDetailResponse> getCardDetail(@RequestParam CardDetailRequest request) {
		return SuccessResponse.of(
			new CardDetailResponse(
				null, null, null, null, null,
				null, null, null, null, null, null));
	}

	@PostMapping("/api/card/scrap")
	public SuccessResponse<ScrapResponse> scrapLink(@RequestParam LinkType type, @RequestBody LinkRequest request) {
		return SuccessResponse.of(
			new ScrapResponse(LinkType.BLOG, "title", "link", "image_url", "description"));
	}

	public void createCard() {

	}
}
