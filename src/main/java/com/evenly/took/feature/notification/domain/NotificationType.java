package com.evenly.took.feature.notification.domain;

import java.util.List;

import com.evenly.took.feature.card.domain.CardInterest;

public enum NotificationType {

	MEMO,
	INTERESTING,
	INTERESTING_DOMAIN,
	INTERESTING_DETAIL_JOB,
	INTERESTING_ORGANIZATION,
	;

	/*
	1. 타입 “한줄메모” = 관심도메인, 세부직군, 소속정보 모두 다른 경우
	2. 타입 “흥미-공통점” = 관심도메인, 세부직군, 소속정보 중 둘 이상이 같은 경우
	3. 타입 “흥미-상세-관심도메인” = 관심도메인 하나만 같은 경우
	4. 타입 “흥미-상세-세부직군” = 세부직군 하나만 같은 경우
	5. 타입 “흥미-상세-소속정보” = 소속정보 하나만 같은 경우
	 */

	private static final int INTERESTING_THRESHOLD = 2;

	public static NotificationType asNotificationType(List<CardInterest> myCards, CardInterest targetCard) {
		if (myCards.isEmpty()) {
			return MEMO;
		}
		CardInterest myCard = myCards.get(0);
		if (myCard.countInterest(targetCard) >= INTERESTING_THRESHOLD) {
			return INTERESTING;
		}
		if (myCard.hasSameInterestDomain(targetCard)) {
			return INTERESTING_DOMAIN;
		}
		if (myCard.hasSameDetailJob(targetCard)) {
			return INTERESTING_DETAIL_JOB;
		}
		if (myCard.hasSameOrganization(targetCard)) {
			return INTERESTING_ORGANIZATION;
		}
		return MEMO;
	}
}
