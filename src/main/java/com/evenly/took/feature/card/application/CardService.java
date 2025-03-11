package com.evenly.took.feature.card.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

	private final CardRepository cardRepository;

	@Transactional(readOnly = true)
	public CardDetailResponse findCardDetail(Long userId, CardDetailRequest request) {
		Card card = cardRepository.findByUserIdAndIdAndDeletedAtIsNull(userId, request.id())
			.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));
		return CardDetailResponse.from(card);
	}
}
