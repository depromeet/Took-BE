package com.evenly.took.feature.notification.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.evenly.took.feature.card.domain.CardInterest;

class NotificationTypeTest {

	@Test
	void 관심도메인_세부직군_소속정보가_같은_명함이_하나도_없으면_MEMO를_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인"));
		List<CardInterest> others = List.of(
			new CardInterest("다름", "다름", List.of("다름")),
			new CardInterest("다름", "다름", List.of("다름")),
			new CardInterest("다름", "다름", List.of("다름")));

		// when
		NotificationType type = NotificationType.asNotificationType(my, others);

		// then
		assertThat(type).isEqualTo(NotificationType.MEMO);
	}

	@Test
	void 관심도메인_세부직군_소속정보_중_세부직군_필드가_같은_명함이_하나라도_있으면_INTERESTING을_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인"));
		List<CardInterest> others = List.of(
			new CardInterest("세부직군", "다름", List.of("다름")),
			new CardInterest("다름", "다름", List.of("다름")),
			new CardInterest("다름", "다름", List.of("다름")));

		// when
		NotificationType type = NotificationType.asNotificationType(my, others);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}

	@Test
	void 관심도메인_세부직군_소속정보_중_소속정보_필드가_같은_명함이_하나라도_있으면_INTERESTING을_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인1", "도메인2"));
		List<CardInterest> others = List.of(
			new CardInterest("다름", "다름", List.of("다름")),
			new CardInterest("다름", "다름", List.of("다름")),
			new CardInterest("다름", "소속정보", List.of("다름")));

		// when
		NotificationType type = NotificationType.asNotificationType(my, others);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}

	@Test
	void 관심도메인_세부직군_소속정보_중_도메인_필드가_같은_명함이_하나라도_있으면_INTERESTING을_반환한다() {
		// given
		CardInterest my = new CardInterest("세부직군", "소속정보", List.of("도메인1", "도메인2"));
		List<CardInterest> others = List.of(
			new CardInterest("다름", "다름", List.of("도메인2")),
			new CardInterest("다름", "다름", List.of("다름")),
			new CardInterest("다름", "다름", List.of("다름")));

		// when
		NotificationType type = NotificationType.asNotificationType(my, others);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}
}
