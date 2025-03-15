package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.dao.CareerRepository;
import com.evenly.took.feature.card.domain.Career;

@Component
public class CareerFixture {

	@Autowired
	CareerRepository careerRepository;

	public Career serverDeveloper() {
		return careerRepository.findById(7L)
			.orElseThrow(() -> new IllegalStateException("데이터 초기화 작업 미흡"));
	}
}
