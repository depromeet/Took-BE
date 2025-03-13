package com.evenly.took.global.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.domain.SNSType;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.domain.vo.SNS;
import com.evenly.took.feature.user.domain.User;

public class TestCardFactory {

	private static final Long DEFAULT_ID = 1L;

	public static Card createDefaultCard() {
		return createCard(cardBuilder -> {
		});
	}

	public static Card createCardWithPreviewType(PreviewInfoType type) {
		return createCard(cardBuilder -> {
			cardBuilder.previewInfo(type);
			applyPreviewInfoData(cardBuilder, type);
		});
	}

	public static Card createCard(Consumer<Card.CardBuilder> customizer) {
		User user = TestUserFactory.createMockGoogleUser();
		Career career = createDefaultCareer();

		Card.CardBuilder cardBuilder = Card.builder()
			.user(user)
			.career(career)
			.nickname("개발자")
			.organization("Evenly")
			.previewInfo(PreviewInfoType.PROJECT)
			.imagePath("profile.jpg")
			.summary("백엔드 개발자입니다")
			.interestDomain(Arrays.asList("Spring", "Java"));

		customizer.accept(cardBuilder);

		Card card = cardBuilder.build();
		ReflectionTestUtils.setField(card, "id", DEFAULT_ID);

		return card;
	}

	public static Career createCareer(Consumer<Career.CareerBuilder> customizer) {
		Career.CareerBuilder careerBuilder = Career.builder()
			.job(Job.DEVELOPER)
			.detailJobKr(List.of("백엔드 개발자"))
			.detailJobEn("Backend Developer");

		customizer.accept(careerBuilder);

		Career career = careerBuilder.build();
		ReflectionTestUtils.setField(career, "id", DEFAULT_ID);

		return career;
	}

	public static Career createDefaultCareer() {
		return createCareer(careerBuilder -> {
		});
	}

	public static Career createCareerWithId(Long id) {
		Career career = createDefaultCareer();
		ReflectionTestUtils.setField(career, "id", id);
		return career;
	}

	private static void applyPreviewInfoData(Card.CardBuilder cardBuilder, PreviewInfoType type) {
		switch (type) {
			case PROJECT:
				cardBuilder.project(Collections.singletonList(
					new Project("테스트 프로젝트", "https://github.com/test", "test-image.jpg", "테스트 프로젝트 설명")
				));
				break;
			case CONTENT:
				cardBuilder.content(Collections.singletonList(
					new Content("테스트 글", "https://blog.com/test", "test-blog-image.jpg", "테스트 글 설명")
				));
				break;
			case HOBBY:
				cardBuilder.hobby("등산, 독서");
				break;
			case SNS:
				cardBuilder.sns(Collections.singletonList(
					new SNS(SNSType.GITHUB, "https://github.com/user")
				));
				break;
			case NEWS:
				cardBuilder.news("최근 블로그 포스팅 시작했습니다");
				break;
			case REGION:
				cardBuilder.region("서울 강남구");
				break;
			default:
				break;
		}
	}

	public static Card createCardWithProject(String title, String link, String imageUrl, String description) {
		return createCard(cardBuilder -> {
			cardBuilder.previewInfo(PreviewInfoType.PROJECT);
			cardBuilder.project(Collections.singletonList(
				new Project(title, link, imageUrl, description)
			));
		});
	}

	public static Card createCardWithContent(String title, String link, String imageUrl, String description) {
		return createCard(cardBuilder -> {
			cardBuilder.previewInfo(PreviewInfoType.CONTENT);
			cardBuilder.content(Collections.singletonList(
				new Content(title, link, imageUrl, description)
			));
		});
	}

	public static Card createCardWithSNS(SNSType type, String link) {
		return createCard(cardBuilder -> {
			cardBuilder.previewInfo(PreviewInfoType.SNS);
			cardBuilder.sns(Collections.singletonList(
				new SNS(type, link)
			));
		});
	}

	public static Card createCardWithProjects(List<Project> projects) {
		return createCard(cardBuilder -> {
			cardBuilder.previewInfo(PreviewInfoType.PROJECT);
			cardBuilder.project(projects);
		});
	}

	public static Card createCardWithContents(List<Content> contents) {
		return createCard(cardBuilder -> {
			cardBuilder.previewInfo(PreviewInfoType.CONTENT);
			cardBuilder.content(contents);
		});
	}

	public static Card createCardWithSNSList(List<SNS> snsList) {
		return createCard(cardBuilder -> {
			cardBuilder.previewInfo(PreviewInfoType.SNS);
			cardBuilder.sns(snsList);
		});
	}
}
