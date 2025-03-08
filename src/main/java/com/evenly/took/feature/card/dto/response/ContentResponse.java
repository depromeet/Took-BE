package com.evenly.took.feature.card.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "작성한 글 정보")
public record ContentResponse(
	@Schema(description = "글 제목", example = "Spring Boot로 RESTful API 만들기")
	String title,

	@Schema(description = "글 링크", example = "https://blog.example.com/posts/123")
	String link,

	@Schema(description = "글 썸네일 이미지", example = "/images/thumbnails/post123.jpg")
	String imageUrl,

	@Schema(description = "글 설명", example = "Spring Boot를 이용한 RESTful API 개발 방법을 소개합니다.")
	String description
) {
}
