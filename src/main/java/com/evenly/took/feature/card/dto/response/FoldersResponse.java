package com.evenly.took.feature.card.dto.response;

import java.util.List;

public record FoldersResponse(
	List<FolderResponse> folders
) {
}
