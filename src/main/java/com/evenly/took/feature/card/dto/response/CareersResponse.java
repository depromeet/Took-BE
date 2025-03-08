package com.evenly.took.feature.card.dto.response;

import java.util.List;

import com.evenly.took.feature.card.domain.Career;

public record CareersResponse(
	List<CareerResponse> careers
) {

	public static CareersResponse from(List<Career> careers) {
		return new CareersResponse(careers.stream()
			.map(CareerResponse::new)
			.toList());
	}
}
