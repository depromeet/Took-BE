package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.dao.ReceivedCardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.user.domain.User;

@Component
public class ReceivedCardFixture extends ReceivedCardBase {

	@Autowired
	ReceivedCardRepository receivedCardRepository;

	@Autowired
	UserFixture userFixture;

	@Autowired
	CardFixture cardFixture;

	public ReceivedCardBase creator() {
		init();
		return this;
	}

	@Override
	public ReceivedCard create() {
		if (user == null) {
			user = userFixture.create();
		}
		if (card == null) {
			// 카드를 받는 사용자와 카드 소유자는 달라야 함
			User cardOwner = userFixture.creator().id(user.getId() + 1).email("cardowner@example.com").create();
			card = cardFixture.creator().user(cardOwner).create();
		}

		ReceivedCard receivedCard = ReceivedCard.builder()
			.user(user)
			.card(card)
			.memo(memo)
			.build();

		ReceivedCard savedReceivedCard = receivedCardRepository.save(receivedCard);

		if (deletedAt != null) {
			ReflectionTestUtils.setField(savedReceivedCard, "deletedAt", deletedAt);
			return receivedCardRepository.save(savedReceivedCard);
		}

		return savedReceivedCard;
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
