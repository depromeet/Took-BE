package com.evenly.took.global.domain;

import java.util.List;

import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Job;

public abstract class CareerBase {

	protected Long id = 7L;
	protected Job job = Job.DEVELOPER;
	protected String detailJobEn = "Server Developer";
	protected List<String> detailJobKr = List.of("서버 개발자");

	public CareerBase id(Long id) {
		this.id = id;
		return this;
	}

	public CareerBase job(Job job) {
		this.job = job;
		return this;
	}

	public CareerBase detailJobEn(String detailJobEn) {
		this.detailJobEn = detailJobEn;
		return this;
	}

	public CareerBase detailJobKr(List<String> detailJobKr) {
		this.detailJobKr = detailJobKr;
		return this;
	}

	public abstract Career create();
}
