package com.evenly.took.feature.card.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.global.service.ServiceTest;

class CardServiceTest extends ServiceTest {

	@Autowired
	CardService cardService;

	@Test
	void 디자인_직군에_해당하는_모든_커리어를_조회한다() {
		// given, when
		CareersResponse response = cardService.findCareers(Job.DESIGNER);

		// then
		assertThat(response.careers()).hasSize(6);
	}

	@Test
	void 개발_직군에_해당하는_모든_커리어를_조회한다() {
		// given, when
		CareersResponse response = cardService.findCareers(Job.DEVELOPER);

		// then
		assertThat(response.careers()).hasSize(11);
	}
}
