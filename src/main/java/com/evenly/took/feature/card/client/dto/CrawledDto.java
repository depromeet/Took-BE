package com.evenly.took.feature.card.client.dto;

import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;

public record CrawledDto(
	String title,
	String link,
	String imageUrl,
	String description
) {
	public Content toContent() {
		return new Content(title, link, imageUrl, description);
	}

	public Project toProject() {
		return new Project(title, link, imageUrl, description);
	}
}
