package com.evenly.took.feature.notification.domain;

import java.util.List;

import com.evenly.took.feature.card.domain.CardInterest;

public enum NotificationType {

	MEMO,
	INTERESTING,
	;

	/*
	1. MEMO = 관심도메인, 세부직군, 소속정보 모두 다른 경우
	2. INTERESTING = 관심도메인, 세부직군, 소속정보 중 하나 이상이 같은 경우
	 */

	public static NotificationType asNotificationType(CardInterest myCard, List<CardInterest> targetCards) {
		if (myCard == null) {
			return MEMO;
		}
		for (CardInterest targetCard : targetCards) {
			if (myCard.isInteresting(targetCard)) {
				return INTERESTING;
			}
		}
		return MEMO;
	}
}
