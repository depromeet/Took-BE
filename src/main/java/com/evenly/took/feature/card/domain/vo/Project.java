package com.evenly.took.feature.card.domain.vo;

import com.evenly.took.feature.card.dto.response.ProjectResponse;

public record Project(
	String title,
	String link,
	String imageUrl,
	String description
) {
	public ProjectResponse toDto() {
		return new ProjectResponse(title, link, imageUrl, description);
	}
}
