package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.domain.ReceivedCard;

public class ReceivedCardFactory extends ReceivedCardBase {

	UserFactory userFactory = new UserFactory();
	CardFactory cardFactory = new CardFactory();

	public ReceivedCardBase creator() {
		return new ReceivedCardFactory();
	}

	@Override
	public ReceivedCard create() {
		if (user == null) {
			user = userFactory.create();
		}
		if (card == null) {
			card = cardFactory.create();
		}

		ReceivedCard receivedCard = ReceivedCard.builder()
			.user(user)
			.card(card)
			.memo(memo)
			.build();

		ReflectionTestUtils.setField(receivedCard, "id", id);

		if (deletedAt != null) {
			ReflectionTestUtils.setField(receivedCard, "deletedAt", deletedAt);
		}

		return receivedCard;
	}
}
