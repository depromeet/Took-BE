package com.evenly.took.feature.card.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.evenly.took.feature.card.domain.vo.SNS;
import com.evenly.took.feature.card.dto.request.SNSRequest;

@Mapper(componentModel = "spring")
public interface SnsMapper {
	List<SNS> toEntity(List<SNSRequest> request);
}
