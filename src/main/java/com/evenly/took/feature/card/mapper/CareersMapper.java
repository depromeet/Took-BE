package com.evenly.took.feature.card.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.dto.response.CareerResponse;
import com.evenly.took.feature.card.dto.response.CareersResponse;

@Mapper(componentModel = "spring")
public interface CareersMapper {

	List<CareerResponse> toCareerResponses(List<Career> entities);

	default CareersResponse toCareersResponse(List<Career> entities) {
		return new CareersResponse(toCareerResponses(entities));
	}
}
