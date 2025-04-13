package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.domain.Career;

public class CareerFactory extends CareerBase {

	public CareerBase creator() {
		return new CareerFactory();
	}

	@Override
	public Career create() {
		Career career = Career.builder()
			.job(job)
			.detailJobEn(detailJobEn)
			.detailJobKr(detailJobKr)
			.build();
		ReflectionTestUtils.setField(career, "id", id);
		return career;
	}
}
