package com.evenly.took.feature.notification.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.global.domain.CardFactory;
import com.evenly.took.global.domain.CareerFactory;

class NotificationTypeTest {

	CardFactory cardFactory = new CardFactory();
	CareerFactory careerFactory = new CareerFactory();

	@Test
	void 관심도메인_세부직군_소속정보가_같은_명함이_하나도_없으면_MEMO를_반환한다() {
		// given
		Card my = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("세부직군").create())
			.organization("소속정보")
			.interestDomain(List.of("도메인"))
			.create();
		Card other1 = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("다름").create())
			.organization("다름")
			.interestDomain(List.of("다름"))
			.create();
		Card other2 = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("다름").create())
			.organization("다름")
			.interestDomain(List.of("다름"))
			.create();
		List<Card> others = List.of(other1, other2);

		// when
		NotificationType type = NotificationType.asNotificationType(my, others);

		// then
		assertThat(type).isEqualTo(NotificationType.MEMO);
	}

	@Test
	void 관심도메인_세부직군_소속정보_중_세부직군_필드가_같은_명함이_하나라도_있으면_INTERESTING을_반환한다() {
		// given
		Card my = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("세부직군").create())
			.organization("소속정보")
			.interestDomain(List.of("도메인"))
			.create();
		Card other1 = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("세부직군").create())
			.organization("다름")
			.interestDomain(List.of("다름"))
			.create();
		Card other2 = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("다름").create())
			.organization("다름")
			.interestDomain(List.of("다름"))
			.create();
		List<Card> others = List.of(other1, other2);

		// when
		NotificationType type = NotificationType.asNotificationType(my, others);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}

	@Test
	void 관심도메인_세부직군_소속정보_중_소속정보_필드가_같은_명함이_하나라도_있으면_INTERESTING을_반환한다() {
		// given
		Card my = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("세부직군").create())
			.organization("소속정보")
			.interestDomain(List.of("도메인1", "도메인2"))
			.create();
		Card other1 = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("다름").create())
			.organization("다름")
			.interestDomain(List.of("다름"))
			.create();
		Card other2 = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("다름").create())
			.organization("소속정보")
			.interestDomain(List.of("다름"))
			.create();
		List<Card> others = List.of(other1, other2);

		// when
		NotificationType type = NotificationType.asNotificationType(my, others);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}

	@Test
	void 관심도메인_세부직군_소속정보_중_도메인_필드가_같은_명함이_하나라도_있으면_INTERESTING을_반환한다() {
		// given
		Card my = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("세부직군").create())
			.organization("소속정보")
			.interestDomain(List.of("도메인1", "도메인2"))
			.create();
		Card other1 = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("다름").create())
			.organization("다름")
			.interestDomain(List.of("다름"))
			.create();
		Card other2 = cardFactory.creator()
			.career(careerFactory.creator().detailJobEn("다름").create())
			.organization("다름")
			.interestDomain(List.of("도메인2"))
			.create();
		List<Card> others = List.of(other1, other2);

		// when
		NotificationType type = NotificationType.asNotificationType(my, others);

		// then
		assertThat(type).isEqualTo(NotificationType.INTERESTING);
	}
}
