package com.evenly.took.feature.notification.client.dto.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ExpoPushReceiptsResponse(

	@JsonInclude(JsonInclude.Include.NON_NULL)
	Map<String, ExpoPushReceiptsData> data,

	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<ExpoEntireFailError> errors
) {

	public List<String> getFailIds(List<String> entireIds) {
		List<String> successIds = getSuccessIds();
		return entireIds.stream()
			.filter(id -> !successIds.contains(id))
			.toList();
	}

	private List<String> getSuccessIds() {
		return data.entrySet().stream()
			.filter(entry -> entry.getValue().isSuccess())
			.map(Map.Entry::getKey)
			.toList();
	}
}
