package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.dao.ReceivedCardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.user.domain.User;

@Component
public class ReceivedCardFixture extends ReceivedCardBase {

	@Autowired
	ReceivedCardRepository receivedCardRepository;

	public ReceivedCardBase creator() {
		init();
		return this;
	}

	@Override
	public ReceivedCard create() {
		if (user == null) {
			throw new IllegalStateException("user를 함께 입력해주세요.");
		}
		if (card == null) {
			throw new IllegalStateException("card를 함께 입력해주세요.");
		}
		ReceivedCard receivedCard = ReceivedCard.builder()
			.user(user)
			.card(card)
			.memo(memo)
			.deletedAt(deletedAt)
			.build();
		return receivedCardRepository.save(receivedCard);
	}

	// 편의 메서드
	public ReceivedCard createForUser(User user, Card card) {
		return creator().user(user).card(card).create();
	}

	public ReceivedCard createWithMemo(String memo) {
		return creator().memo(memo).create();
	}

	public ReceivedCard createWithMemoForUser(String memo, User user, Card card) {
		return creator().memo(memo).user(user).card(card).create();
	}
}
