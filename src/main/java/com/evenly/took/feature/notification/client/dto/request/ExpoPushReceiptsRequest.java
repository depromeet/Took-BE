package com.evenly.took.feature.notification.client.dto.request;

import java.util.List;

public record ExpoPushReceiptsRequest(
	List<String> ids
) {
}
