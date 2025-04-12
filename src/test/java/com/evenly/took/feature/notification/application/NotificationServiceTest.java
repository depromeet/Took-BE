package com.evenly.took.feature.notification.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
	void 알림을_전송한다() {
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
		LocalDateTime sendAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
		notificationService.sendNotification(sendAt);

		// then
		assertThat(notificationFixture.count()).isEqualTo(1);
	}
}
