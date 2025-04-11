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

	public int countInterest(CardInterest other) {
		int count = 0;
		if (hasSameInterestDomain(other)) {
			count++;
		}
		if (hasSameDetailJob(other)) {
			count++;
		}
		if (hasSameOrganization(other)) {
			count++;
		}
		return count;
	}

	public boolean hasSameInterestDomain(CardInterest other) {
		return interestDomain.stream().anyMatch(other.interestDomain::contains);
	}

	public boolean hasSameDetailJob(CardInterest other) {
		return detailJob.equals(other.detailJob);
	}

	public boolean hasSameOrganization(CardInterest other) {
		return organization.equals(other.organization);
	}
}
