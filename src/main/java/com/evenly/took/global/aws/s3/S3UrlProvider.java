package com.evenly.took.global.aws.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class S3UrlProvider {

	private static S3Service s3Service;

	@Autowired
	public S3UrlProvider(S3Service s3Service) {
		S3UrlProvider.s3Service = s3Service;
	}

	public static String getFullS3Url(String imagePath) {
		if (imagePath == null || imagePath.isEmpty()) {
			return null;
		}
		return s3Service.getFullS3Url(imagePath);
	}
}
