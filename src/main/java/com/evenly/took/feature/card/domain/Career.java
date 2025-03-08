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
import jakarta.validation.constraints.NotNull;
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

	@Column(name = "job")
	@Enumerated(EnumType.STRING)
	@NotNull
	private Job job;

	@Column(name = "detail_job_kr")
	@JdbcTypeCode(SqlTypes.JSON)
	@NotNull
	private List<String> detailJobKr;

	@Column(name = "detail_job_en")
	@NotNull
	private String detailJobEn;

	@Builder
	public Career(Job job, List<String> detailJobKr, String detailJobEn) {
		this.job = job;
		this.detailJobKr = detailJobKr;
		this.detailJobEn = detailJobEn;
	}
}
