package com.evenly.took.feature.card.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.card.dto.response.FolderResponse;
import com.evenly.took.feature.card.dto.response.FoldersResponse;

@Mapper(componentModel = "spring")
public interface FolderMapper {
	List<FolderResponse> toFolderResponseList(List<Folder> folders);

	default FoldersResponse toFoldersResponse(List<Folder> entities) {
		return new FoldersResponse(toFolderResponseList(entities));
	}
}
