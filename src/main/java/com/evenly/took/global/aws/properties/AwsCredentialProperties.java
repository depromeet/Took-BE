package com.evenly.took.global.aws.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.credentials")
public record AwsCredentialProperties(
	String accessKey,
	String secretKey
) {
}
