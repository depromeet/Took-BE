package com.evenly.took.feature.notification.domain;

import java.util.List;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.CardInterest;

public enum NotificationType {

	MEMO,
	INTERESTING,
	SYSTEM,
	;

	/*
	1. MEMO = 모든 받은 명함의 관심도메인, 세부직군, 소속정보 모두 다른 경우
	2. INTERESTING = 받은 명함 중 하나라도 관심도메인, 세부직군, 소속정보 중 하나 이상이 같은 경우
	 */

	public static NotificationType asNotificationType(Card myCard, List<Card> otherCards) {
		if (myCard == null) {
			return MEMO;
		}
		CardInterest myCardInterest = CardInterest.from(myCard);
		List<CardInterest> otherCardsInterest = otherCards.stream()
			.map(CardInterest::from)
			.toList();
		if (otherCardsInterest.stream().anyMatch(myCardInterest::isInteresting)) {
			return INTERESTING;
		}
		return MEMO;
	}
}
