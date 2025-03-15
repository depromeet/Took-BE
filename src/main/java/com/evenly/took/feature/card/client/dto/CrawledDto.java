package com.evenly.took.feature.card.client.dto;

public record CrawledDto(
	String title,
	String link,
	String imageUrl,
	String description
) {
}
