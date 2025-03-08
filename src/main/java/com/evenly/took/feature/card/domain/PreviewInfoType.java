package com.evenly.took.feature.card.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PreviewInfoType {

	PROJECT("대표 프로젝트"),
	CONTENT("작성한 글"),
	HOBBY("취미"),
	SNS("SNS"),
	NEWS("최근 소식"),
	REGION("활동 지역"),
	;

	private final String value;
}
