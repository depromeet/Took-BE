package com.evenly.took.feature.notification.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.evenly.took.feature.card.domain.CardInterest;

class NotificationTypeTest {

	@Test
	void 관심도메인_세부직군_소속정보_모두_다르면_MEMO를_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인"));
		CardInterest other = new CardInterest("다름", "다름", List.of("다름"));

		// when
		NotificationType type = NotificationType.asNotificationType(List.of(my), other);

		// then
		assertThat(type).isEqualTo(NotificationType.MEMO);
	}

	@Test
	void 세부직군_소속정보이_같으면_INTERESTING을_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인"));
		CardInterest other = new CardInterest("세부직군", "소속정보", List.of("다름"));

		// when
		NotificationType type = NotificationType.asNotificationType(List.of(my), other);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}

	@Test
	void 세부직군_관심도메인이_같으면_INTERESTING을_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인1", "도메인2"));
		CardInterest other = new CardInterest("세부직군", "다름", List.of("도메인1"));

		// when
		NotificationType type = NotificationType.asNotificationType(List.of(my), other);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}

	@Test
	void 소속정보_관심도메인이_같으면_INTERESTING을_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인1", "도메인2"));
		CardInterest other = new CardInterest("다름", "소속정보", List.of("도메인1", "도메인2"));

		// when
		NotificationType type = NotificationType.asNotificationType(List.of(my), other);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}

	@Test
	void 관심도메인만_같으면_INTERESTING_DOMAIN를_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인1", "도메인2"));
		CardInterest other = new CardInterest("다름", "다름", List.of("도메인1"));

		// when
		NotificationType type = NotificationType.asNotificationType(List.of(my), other);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING_DOMAIN);
	}

	@Test
	void 세부직군만_같으면_INTERESTING_DETAIL_JOB를_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인1", "도메인2"));
		CardInterest other = new CardInterest("세부직군", "다름", List.of("다름"));

		// when
		NotificationType type = NotificationType.asNotificationType(List.of(my), other);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING_DETAIL_JOB);
	}

	@Test
	void 소속정보만_같으면_INTERESTING_ORGANIZATION를_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인1", "도메인2"));
		CardInterest other = new CardInterest("다름", "소속정보", List.of("다름"));

		// when
		NotificationType type = NotificationType.asNotificationType(List.of(my), other);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING_ORGANIZATION);
	}
}
