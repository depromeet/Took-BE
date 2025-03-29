package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;

@Component
public class CardFixture extends CardBase {

	@Autowired
	CardRepository cardRepository;

	@Autowired
	UserFixture userFixture;

	@Autowired
	CareerFixture careerFixture;

	public CardBase creator() {
		init();
		return this;
	}

	@Override
	public Card create() {
		if (user == null) {
			userFixture.creator().create();
		}
		if (career == null) {
			career = careerFixture.serverDeveloper();
		}
		Card card = Card.builder()
			.user(user)
			.career(careerFixture.serverDeveloper())
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
		return cardRepository.save(card);
	}
}
