package com.evenly.took.feature.notification.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.service.ServiceTest;

@Disabled
class NotificationServiceTest extends ServiceTest {

	@Autowired
	NotificationService notificationService;

	@Test
	void 오전_10시_알림을_전송한다() { // 실행 시간 범위: 00:00 ~ 05:59 || 21:01 ~ 23:59
		LocalTime nowTime = LocalTime.now();
		if (nowTime.isAfter(LocalTime.of(5, 59)) && nowTime.isBefore(LocalTime.of(21, 1))) {
			return;
		}
		// given
		User me = userFixture.creator()
			.allowPushNotification(true)
			.create();
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

	@Test
	void 오후_10시_알림을_전송한다() { // 실행 시간 범위: 06:00 ~ 21:00
		// given
		Clock fixedClock = Clock.fixed(
			LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0))
				.atZone(ZoneId.systemDefault()).toInstant(),
			ZoneId.systemDefault()
		);
		LocalTime nowTime = LocalTime.now();
		if (nowTime.isBefore(LocalTime.of(6, 0)) || nowTime.isAfter(LocalTime.of(21, 0))) {
			return;
		}
		User me = userFixture.creator()
			.allowPushNotification(true)
			.create();
		User cardOwner = userFixture.create();
		Card card = cardFixture.creator()
			.user(cardOwner)
			.create();
		ReceivedCard receivedCard = receivedCardFixture.creator()
			.user(me)
			.card(card)
			.create();

		// when
		LocalDateTime sendAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0));
		notificationService.sendNotification(sendAt);

		// then
		assertThat(notificationFixture.count()).isEqualTo(1);
	}
}
