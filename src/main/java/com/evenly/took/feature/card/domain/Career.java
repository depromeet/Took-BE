package com.evenly.took.feature.card.domain;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "careers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Career {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "job", nullable = false)
	@Enumerated(EnumType.STRING)
	private Job job;

	@Column(name = "detail_job_en", nullable = false)
	private String detailJobEn;

	@Column(name = "detail_job_kr", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> detailJobKr;

	@Builder
	public Career(Job job, String detailJobEn, List<String> detailJobKr) {
		this.job = job;
		this.detailJobEn = detailJobEn;
		this.detailJobKr = detailJobKr;
	}

	public static Career toEntity(Long id) {
		Career careerEntity = new Career();
		careerEntity.id = id;
		return careerEntity;
	}
}
