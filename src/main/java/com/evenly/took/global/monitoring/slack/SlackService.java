package com.evenly.took.global.monitoring.slack;

import static com.slack.api.webhook.WebhookPayloads.payload;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;

@Profile({"prod"})
@Service
public class SlackService {

	private static final String ERROR_COLOR_CODE = "#FF0000";

	@Value("${webhook.slack.url}")
	private String slackUrl;

	private final Slack slackClient = Slack.getInstance();

	public void sendMessage(String title, Map<String, String> data) {
		try {
			List<Field> fields = data.entrySet().stream()
				.map(entry -> Field.builder()
					.title(entry.getKey())
					.value(entry.getValue() == null ? "" : entry.getValue())
					.valueShortEnough(false)
					.build())
				.collect(Collectors.toList());

			slackClient.send(slackUrl, payload(p -> p
				.text(title)
				.attachments(List.of(
					Attachment.builder()
						.color(ERROR_COLOR_CODE)
						.fields(fields)
						.build()
				)))
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
