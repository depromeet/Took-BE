package com.evenly.took.feature.card.domain;

public enum LinkType {

	BLOG,
	PROJECT,
	;

	public boolean isBlog() {
		return this == BLOG;
	}
}
