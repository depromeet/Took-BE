package com.evenly.took.feature.card.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.evenly.took.feature.card.dao.CareerRepository;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.LinkType;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.feature.card.mapper.CareersMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

	private final CareerRepository careerRepository;
	private final LinkExtractor linkExtractor;
	private final CareersMapper careersMapper;

	public CareersResponse findCareers(Job job) {
		List<Career> careers = careerRepository.findAllByJob(job);
		return careersMapper.toResponse(careers);
	}

	public ScrapResponse scrapLink(LinkType type, LinkRequest request) {
		if (type.isBlog()) {
			Content content = linkExtractor.extractContent(request.link());
			return ScrapResponse.toResponse(content);
		}
		Project project = linkExtractor.extractProject(request.link());
		return ScrapResponse.toResponse(project);
	}
}
