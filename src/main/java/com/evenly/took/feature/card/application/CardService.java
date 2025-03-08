package com.evenly.took.feature.card.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.MyCardResponse;
import com.evenly.took.feature.card.mapper.CardMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

	private final CardRepository cardRepository;
	private final CardMapper cardMapper;

	@Transactional(readOnly = true)
	public MyCardListResponse findUserCardList(Long userId) {
		List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(userId);
		List<MyCardResponse> myCardResponses = cardMapper.toMyCardResponseList(cards);
		return new MyCardListResponse(myCardResponses);
	}
}
