package com.evenly.took.feature.notification.client.dto.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;

public record ExpoPushReceiptsData(

	String status,

	@JsonAlias("message")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	String errorMessage,

	@JsonAlias("details")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	Map<String, String> errorDetails
) {

	public boolean isSuccess() {
		return status.equals("ok");
	}

	public boolean isFail() {
		return status.equals("error");
	}

	public String getErrorResponse() {
		// DeviceNotRegistered, MessageTooBig, MessageRateExceeded, MismatchSenderId, InvalidCredentials
		return errorDetails.get("error");
	}
}
