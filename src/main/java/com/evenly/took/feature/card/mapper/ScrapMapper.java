package com.evenly.took.feature.card.mapper;

import org.mapstruct.Mapper;

import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.feature.card.dto.response.ScrapResponse;

@Mapper(componentModel = "spring")
public interface ScrapMapper {

	ScrapResponse toScrapResponse(CrawledDto crawledDto);
}
