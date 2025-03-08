package com.evenly.took.feature.card.api;

import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.feature.card.dto.response.MyCardListResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {

	@Override
	public void getJobCategories() {

	}

	@Override
	public MyCardListResponse getMyCards() {
		return new MyCardListResponse(null);
	}

	@Override
	public void getCardDetail() {

	}

	@Override
	public void scrapExternalContent() {

	}

	@Override
	public void createCard() {

	}
}
