package com.evenly.took.feature.card.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardImage {

	@Column(name = "image_path")
	private String imagePath;

	@Builder
	public CardImage(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return CardImageUrlFetcher.getImageUrl(imagePath);
	}

	public String getRawImagePath() {
		return imagePath;
	}
}
