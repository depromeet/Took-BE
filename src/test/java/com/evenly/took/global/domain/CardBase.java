package com.evenly.took.global.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.domain.SNSType;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.domain.vo.SNS;
import com.evenly.took.feature.user.domain.User;

public abstract class CardBase {

	static Long DEFAULT_ID = 1L;
	static User DEFAULT_USER = null;
	static Career DEFAULT_CAREER = null;
	static String DEFAULT_NICKNAME = "닉네임";
	static String DEFAULT_IMAGE_PATH = "/image/path";
	static String DEFAULT_SUMMARY = "안녕하세요 이븐하게 백엔드 팀 임손나입니다.";
	static String DEFAULT_ORGANIZATION = "디프만";
	static String DEFAULT_REGION = "서울";
	static String DEFAULT_HOBBY = "수영, 3D펜, 헬스";
	static String DEFAULT_NEWS = "디프만 동아리에서 툭 서비스를 만들고 있어요";
	static PreviewInfoType DEFAULT_PREVIEW_INFO = PreviewInfoType.NEWS;
	static List<String> DEFAULT_INTEREST_DOMAIN = List.of("백엔드", "데브옵스", "보안");
	static List<SNS> DEFAULT_SNS = List.of(new SNS(SNSType.GITHUB, "https://github.com/depromeet"));
	static List<Content> DEFAULT_CONTENTS = List.of(new Content("제목", "링크", "이미지", "설명"));
	static List<Project> DEFAULT_PROJECTS = List.of(new Project("제목", "링크", "이미지", "설명"));
	static LocalDateTime DEFAULT_DELETED_AT = null;

	Long id;
	User user;
	Career career;
	String nickname;
	String imagePath;
	String summary;
	String organization;
	String region;
	String hobby;
	String news;
	PreviewInfoType previewInfo;
	List<String> interestDomain;
	List<SNS> sns;
	List<Content> contents;
	List<Project> projects;
	LocalDateTime deletedAt;

	protected CardBase() {
		init();
	}

	protected void init() {
		this.id = DEFAULT_ID;
		this.user = DEFAULT_USER;
		this.career = DEFAULT_CAREER;
		this.nickname = DEFAULT_NICKNAME;
		this.imagePath = DEFAULT_IMAGE_PATH;
		this.summary = DEFAULT_SUMMARY;
		this.organization = DEFAULT_ORGANIZATION;
		this.region = DEFAULT_REGION;
		this.hobby = DEFAULT_HOBBY;
		this.news = DEFAULT_NEWS;
		this.previewInfo = DEFAULT_PREVIEW_INFO;
		this.interestDomain = DEFAULT_INTEREST_DOMAIN;
		this.sns = DEFAULT_SNS;
		this.contents = DEFAULT_CONTENTS;
		this.projects = DEFAULT_PROJECTS;
		this.deletedAt = DEFAULT_DELETED_AT;
	}

	public CardBase id(Long id) {
		this.id = id;
		return this;
	}

	public CardBase user(User user) {
		this.user = user;
		return this;
	}

	public CardBase career(Career career) {
		this.career = career;
		return this;
	}

	public CardBase previewInfo(PreviewInfoType previewInfo) {
		this.previewInfo = previewInfo;
		return this;
	}

	public CardBase nickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public CardBase imagePath(String imagePath) {
		this.imagePath = imagePath;
		return this;
	}

	public CardBase interestDomain(List<String> interestDomain) {
		this.interestDomain = interestDomain;
		return this;
	}

	public CardBase summary(String summary) {
		this.summary = summary;
		return this;
	}

	public CardBase organization(String organization) {
		this.organization = organization;
		return this;
	}

	public CardBase region(String region) {
		this.region = region;
		return this;
	}

	public CardBase hobby(String hobby) {
		this.hobby = hobby;
		return this;
	}

	public CardBase news(String news) {
		this.news = news;
		return this;
	}

	public CardBase sns(List<SNS> sns) {
		this.sns = sns;
		return this;
	}

	public CardBase contents(List<Content> contents) {
		this.contents = contents;
		return this;
	}

	public CardBase projects(List<Project> projects) {
		this.projects = projects;
		return this;
	}

	public CardBase deletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
		return this;
	}

	public abstract Card create();
}
