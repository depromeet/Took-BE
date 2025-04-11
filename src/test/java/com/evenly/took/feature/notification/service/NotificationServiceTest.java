package com.evenly.took.feature.notification.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.service.ServiceTest;

class NotificationServiceTest extends ServiceTest {

	@Autowired
	NotificationService notificationService;

	@Test
	void 알림을_예약한다() {
		// given
		User me = userFixture.create();
		User cardOwner = userFixture.create();
		Card card = cardFixture.creator()
			.user(cardOwner)
			.create();
		ReceivedCard receivedCard = receivedCardFixture.creator()
			.user(me)
			.card(card)
			.create();

		// when
		notificationService.reserveNotification(receivedCard);

		// then
		assertThat(notificationFixture.count()).isEqualTo(1);
	}
}
