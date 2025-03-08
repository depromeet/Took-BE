package com.evenly.took.global.aws.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import software.amazon.awssdk.regions.Region;

@ConfigurationProperties(prefix = "aws")
public record AwsProperties(
	Region region,
	AwsCredentialProperties credentials,
	AwsS3Properties s3
) {
}
