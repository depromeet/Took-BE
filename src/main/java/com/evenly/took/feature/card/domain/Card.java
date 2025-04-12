package com.evenly.took.feature.card.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.domain.vo.SNS;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.feature.common.model.BaseTimeEntity;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Card extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "career_id")
	private Career career;

	@Column(name = "preview_info", nullable = false)
	@Enumerated(EnumType.STRING)
	private PreviewInfoType previewInfo;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "image_path", nullable = false)
	private String imagePath;

	@Column(name = "interest_domain", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> interestDomain;

	@Column(name = "summary", nullable = false)
	private String summary;

	@Column(name = "organization")
	private String organization;

	@Column(name = "sns")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<SNS> sns;

	@Column(name = "region")
	private String region;

	@Column(name = "hobby")
	private String hobby;

	@Column(name = "news")
	private String news;

	@Column(name = "content")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<Content> content;

	@Column(name = "project")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<Project> project;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "isPrimary")
	private boolean isPrimary;

	@Builder
	public Card(Career career, List<Content> content, String hobby, String imagePath, List<String> interestDomain,
		LocalDateTime deletedAt, String news, String nickname, String organization, PreviewInfoType previewInfo,
		List<Project> project, String region, List<SNS> sns, String summary, User user, boolean isPrimary) {
		this.career = career;
		this.content = content;
		this.hobby = hobby;
		this.imagePath = imagePath;
		this.deletedAt = deletedAt;
		this.news = news;
		this.nickname = nickname;
		this.organization = organization;
		this.previewInfo = previewInfo;
		this.project = project;
		this.region = region;
		this.sns = sns;
		this.summary = summary;
		this.user = user;
		this.interestDomain = interestDomain;
		this.isPrimary = isPrimary;
	}

	public void setImageLink(String signedImageLink) {
		this.imagePath = signedImageLink;
	}

	public void softDelete(Long userId) {
		validateOwner(userId);
		this.deletedAt = LocalDateTime.now();
	}

	private void validateOwner(Long userId) {
		if (!userId.equals(this.user.getId())) {
			throw new TookException(CardErrorCode.INVALID_CARD_OWNER);
		}
	}

	public void changePrimaryCard(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
}
