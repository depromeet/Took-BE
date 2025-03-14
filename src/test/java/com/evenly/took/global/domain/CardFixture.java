package com.evenly.took.global.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.domain.SNSType;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.domain.vo.SNS;
import com.evenly.took.feature.user.domain.User;

@Component
public class CardFixture {

	private static final String DEFAULT_REGION = "서울";
	private static final String DEFAULT_NICKNAME = "닉네임";
	private static final String DEFAULT_HOBBY = "수영, 3D펜, 헬스";
	private static final String DEFAULT_SUMMARY = "안녕하세요 이븐하게 백엔드 팀 임손나입니다.";
	private static final String DEFAULT_NEWS = "디프만 동아리에서 툭 서비스를 만들고 있어요";
	private static final String DEFAULT_IMAGE_PATH = "/image/path";
	private static final String DEFAULT_ORGANIZATION = "디프만";
	private static final PreviewInfoType DEFAULT_PREVIEW_INFO = PreviewInfoType.NEWS;
	private static final List<String> DEFAULT_INTEREST_DOMAIN = List.of("백엔드", "데브옵스", "보안");
	private static final List<SNS> DEFAULT_SNS = List.of(new SNS(SNSType.GITHUB, "https://github.com/depromeet"));
	private static final List<Content> DEFAULT_CONTENT = List.of(new Content("제목", "링크", "이미지", "설명"));
	private static final List<Project> DEFAULT_PROJECT = List.of(new Project("제목", "링크", "이미지", "설명"));

	@Autowired
	CardRepository cardRepository;

	@Autowired
	CareerFixture careerFixture;

	public Card createCard(User user) {
		return createCard(user,
			careerFixture.serverDeveloper(),
			DEFAULT_PREVIEW_INFO,
			DEFAULT_NICKNAME,
			DEFAULT_IMAGE_PATH,
			DEFAULT_INTEREST_DOMAIN,
			DEFAULT_SUMMARY,
			DEFAULT_ORGANIZATION,
			DEFAULT_SNS,
			DEFAULT_REGION,
			DEFAULT_HOBBY,
			DEFAULT_NEWS,
			DEFAULT_CONTENT,
			DEFAULT_PROJECT);
	}

	public Card createCard(User user, String nickname, PreviewInfoType previewInfoType) {
		return createCard(user,
			careerFixture.serverDeveloper(),
			previewInfoType,
			nickname,
			DEFAULT_IMAGE_PATH,
			DEFAULT_INTEREST_DOMAIN,
			DEFAULT_SUMMARY,
			DEFAULT_ORGANIZATION,
			DEFAULT_SNS,
			DEFAULT_REGION,
			DEFAULT_HOBBY,
			DEFAULT_NEWS,
			DEFAULT_CONTENT,
			DEFAULT_PROJECT);
	}

	public Card createCard(User user, String nickname, String organization, String region) {
		return createCard(user,
			careerFixture.serverDeveloper(),
			DEFAULT_PREVIEW_INFO,
			nickname,
			DEFAULT_IMAGE_PATH,
			DEFAULT_INTEREST_DOMAIN,
			DEFAULT_SUMMARY,
			organization,
			DEFAULT_SNS,
			region,
			DEFAULT_HOBBY,
			DEFAULT_NEWS,
			DEFAULT_CONTENT,
			DEFAULT_PROJECT);
	}

	public Card createCard(User user, List<SNS> sns, List<Content> contents, List<Project> projects) {
		return createCard(user,
			careerFixture.serverDeveloper(),
			DEFAULT_PREVIEW_INFO,
			DEFAULT_NICKNAME,
			DEFAULT_IMAGE_PATH,
			DEFAULT_INTEREST_DOMAIN,
			DEFAULT_SUMMARY,
			DEFAULT_ORGANIZATION,
			sns,
			DEFAULT_REGION,
			DEFAULT_HOBBY,
			DEFAULT_NEWS,
			contents,
			projects);
	}

	private Card createCard(User user,
		Career career,
		PreviewInfoType previewInfoType,
		String nickname,
		String imagePath,
		List<String> interestDomain,
		String summary,
		String organization,
		List<SNS> sns,
		String region,
		String hobby,
		String news,
		List<Content> contents,
		List<Project> projects) {

		Card card = Card.builder()
			.user(user)
			.career(career)
			.previewInfo(previewInfoType)
			.nickname(nickname)
			.imagePath(imagePath)
			.interestDomain(interestDomain)
			.summary(summary)
			.organization(organization)
			.sns(sns)
			.region(region)
			.hobby(hobby)
			.news(news)
			.content(contents)
			.project(projects)
			.build();
		return cardRepository.save(card);
	}
}
