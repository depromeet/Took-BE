package com.evenly.took.global.config.sqs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.evenly.took.global.aws.properties.AwsProperties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@RequiredArgsConstructor
public class SQSConfig {

	private final AwsProperties awsProperties;

	@Bean
	public SqsClient sqsClient() {
		return SqsClient.builder()
			.region(awsProperties.region())
			.credentialsProvider(
				StaticCredentialsProvider.create(
					AwsBasicCredentials.create(
						awsProperties.credentials().accessKey(),
						awsProperties.credentials().secretKey()
					)
				)
			)
			.build();
	}
}
