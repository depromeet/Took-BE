package com.evenly.took.feature.card.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.dto.request.ProjectRequest;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
	List<Project> toEntity(List<ProjectRequest> request);
}
