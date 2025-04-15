package com.evenly.took.feature.notification.client.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ExpoPushTicketsResponse(

	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<ExpoPushTicketsData> data,

	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<ExpoEntireFailError> errors
) {

	public List<String> getSuccessTicketIds() {
		return data.stream()
			.filter((ExpoPushTicketsData::isSuccess))
			.map(ExpoPushTicketsData::id)
			.toList();
	}
}
