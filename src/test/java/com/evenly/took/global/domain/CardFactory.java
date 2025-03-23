package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.domain.Card;

public class CardFactory extends CardBase {

	UserFactory userFactory;
	CareerFactory careerFactory;

	public CardBase creator() {
		return new CardFactory();
	}

	@Override
	public Card create() {
		if (user == null) {
			user = userFactory.create();
		}
		if (career == null) {
			career = careerFactory.create();
		}
		Card card = Card.builder()
			.user(user)
			.career(career)
			.previewInfo(previewInfo)
			.nickname(nickname)
			.imagePath(imagePath)
			.interestDomain(interestDomain)
			.summary(summary)
			.organization(organization)
			.sns(sns)
			.region(region)
			.hobby(hobby)
			.news(news)
			.content(contents)
			.project(projects)
			.build();
		ReflectionTestUtils.setField(card, "id", id);
		return card;
	}
}
