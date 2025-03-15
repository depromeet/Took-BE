package com.evenly.took.feature.card.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.dto.request.ContentRequest;

@Mapper(componentModel = "spring")
public interface ContentMapper {
	List<Content> toEntity(List<ContentRequest> request);
}
