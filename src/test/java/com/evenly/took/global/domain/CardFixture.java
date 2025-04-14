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
	CareerFixture careerFixture;

	public CardBase creator() {
		init();
		return this;
	}

	@Override
	public Card create() {
		if (user == null) {
			throw new IllegalStateException("user를 함께 입력해주세요.");
		}
		if (career == null) {
			career = careerFixture.serverDeveloper();
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
			.isPrimary(isPrimary)
			.build();
		return cardRepository.save(card);
	}
}
