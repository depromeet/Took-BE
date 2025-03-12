package com.evenly.took.feature.card.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.feature.card.dao.CareerRepository;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.mapper.CareersMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

	private final CardRepository cardRepository;
	private final CareerRepository careerRepository;
	private final CareersMapper careersMapper;

	public CareersResponse findCareers(Job job) {
		List<Career> careers = careerRepository.findAllByJob(job);
		return careersMapper.toResponse(careers);
	}

	@Transactional(readOnly = true)
	public CardDetailResponse findCardDetail(Long userId, CardDetailRequest request) {
		Card card = cardRepository.findByUserIdAndIdAndDeletedAtIsNull(userId, request.cardId())
			.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));
		return CardDetailResponse.from(card);
	}
}
