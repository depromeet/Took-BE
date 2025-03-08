package com.evenly.took.global.config.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;

import com.evenly.took.global.aws.properties.AwsCredentialProperties;
import com.evenly.took.global.aws.properties.AwsProperties;
import com.evenly.took.global.aws.properties.AwsS3Properties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@TestConfiguration
public class S3TestConfig {

	private LocalStackContainer localstack;
	private final String TEST_BUCKET = "test-bucket";
	private final String TEST_ENV = "test-env/";

	@PostConstruct
	public void startLocalStack() {
		localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
			.withServices(Service.S3);
		localstack.start();

		// S3 버킷 생성
		S3Client s3Client = S3Client.builder()
			.endpointOverride(localstack.getEndpointOverride(Service.S3))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
			))
			.region(Region.of(localstack.getRegion()))
			.build();

		s3Client.createBucket(req -> req.bucket(TEST_BUCKET));
	}

	@PreDestroy
	public void stopLocalStack() {
		if (localstack != null && localstack.isRunning()) {
			localstack.stop();
		}
	}

	@Bean
	@Primary
	public S3Client testS3Client() {
		return S3Client.builder()
			.endpointOverride(localstack.getEndpointOverride(Service.S3))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
			))
			.region(Region.of(localstack.getRegion()))
			.build();
	}

	@Bean
	@Primary
	public S3Presigner testS3Presigner() {
		return S3Presigner.builder()
			.endpointOverride(localstack.getEndpointOverride(Service.S3))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
			))
			.region(Region.of(localstack.getRegion()))
			.build();
	}

	@Bean
	@Primary
	public AwsCredentialProperties testAwsCredentialProperties() {
		return new AwsCredentialProperties(
			localstack.getAccessKey(),
			localstack.getSecretKey()
		);
	}

	@Bean
	@Primary
	public AwsS3Properties testAwsS3Properties() {
		return new AwsS3Properties(
			TEST_BUCKET,
			TEST_ENV
		);
	}

	@Bean
	@Primary
	public AwsProperties testAwsProperties(AwsCredentialProperties credentialProperties, AwsS3Properties s3Properties) {
		return new AwsProperties(
			Region.of(localstack.getRegion()),
			credentialProperties,
			s3Properties
		);
	}
}
