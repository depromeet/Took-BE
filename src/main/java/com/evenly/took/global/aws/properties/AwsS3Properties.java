package com.evenly.took.global.aws.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record AwsS3Properties(
	String bucket,
	String env
) {
}
