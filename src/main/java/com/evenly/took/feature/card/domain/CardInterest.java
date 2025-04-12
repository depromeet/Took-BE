package com.evenly.took.feature.card.domain;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CardInterest {

	private final String detailJob;
	private final String organization;
	private final List<String> interestDomain;

	public static CardInterest from(Card entity) {
		return new CardInterest(entity.getCareer().getDetailJobEn(),
			entity.getOrganization(),
			entity.getInterestDomain());
	}

	public boolean isInteresting(CardInterest other) {
		return hasSameInterestDomain(other) || hasSameDetailJob(other) || hasSameOrganization(other);
	}

	private boolean hasSameInterestDomain(CardInterest other) {
		return interestDomain.stream().anyMatch(other.interestDomain::contains);
	}

	private boolean hasSameDetailJob(CardInterest other) {
		return detailJob.equals(other.detailJob);
	}

	private boolean hasSameOrganization(CardInterest other) {
		return organization.equals(other.organization);
	}
}
