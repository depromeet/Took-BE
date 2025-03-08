package com.evenly.took.feature.card.dto.response;

import java.util.List;

public record JobsResponse(
	List<JobResponse> jobs
) {
}
