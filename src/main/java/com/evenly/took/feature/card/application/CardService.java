package com.evenly.took.feature.card.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.evenly.took.feature.card.dao.CareerRepository;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.dto.response.CareersResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

	private final CareerRepository careerRepository;

	public CareersResponse fetchJobs(Job job) {
		List<Career> careers = careerRepository.findAllByJob(job);
		return CareersResponse.from(careers);
	}
}
