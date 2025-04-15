package com.evenly.took.feature.notification.client.dto.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;

public record ExpoPushTicketsData(

	String status,

	@JsonInclude(JsonInclude.Include.NON_NULL)
	String id,

	@JsonAlias("message")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	String errorMessage,

	@JsonAlias("details")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	Map<String, Object> errorDetails
) {

	public boolean isSuccess() {
		return status.equals("ok");
	}

	public boolean isFail() {
		return status.equals("error");
	}
}
