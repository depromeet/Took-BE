package com.evenly.took.feature.card.domain.vo;

import com.evenly.took.feature.card.dto.response.ContentResponse;

public record Content(
	String title,
	String link,
	String imageUrl,
	String description
) {
	public ContentResponse toDto() {
		return new ContentResponse(title, link, imageUrl, description);
	}
}
