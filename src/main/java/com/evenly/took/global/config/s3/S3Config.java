package com.evenly.took.global.config.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.evenly.took.global.aws.properties.AwsProperties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class S3Config {

	private final AwsProperties awsProperties;

	@Bean
	public AwsCredentials basicAWSCredentials() {
		return AwsBasicCredentials.create(awsProperties.credentials().accessKey(),
			awsProperties.credentials().secretKey());
	}

	@Bean
	public S3Presigner s3Presigner(AwsCredentials awsCredentials) {
		return S3Presigner.builder()
			.region(awsProperties.region())
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.build();
	}

	@Bean
	public S3Client s3Client(AwsCredentials awsCredentials) {
		return S3Client.builder()
			.region(awsProperties.region())
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.build();
	}
}
