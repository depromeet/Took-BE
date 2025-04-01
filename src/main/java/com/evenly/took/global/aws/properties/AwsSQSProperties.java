package com.evenly.took.global.aws.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.sqs")
public record AwsSQSProperties(
	String crawlQueue
) {
}
