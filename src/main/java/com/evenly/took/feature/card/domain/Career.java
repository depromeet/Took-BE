package com.evenly.took.feature.card.domain;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.evenly.took.feature.common.model.BaseTimeEntity;

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
public class Career extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "job", nullable = false)
	@Enumerated(EnumType.STRING)
	private Job job;

	@Column(name = "detail_job_kr", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> detailJobKr;

	@Column(name = "detail_job_en", nullable = false)
	private String detailJobEn;

	@Builder
	public Career(Job job, List<String> detailJobKr, String detailJobEn) {
		this.job = job;
		this.detailJobKr = detailJobKr;
		this.detailJobEn = detailJobEn;
	}
}
