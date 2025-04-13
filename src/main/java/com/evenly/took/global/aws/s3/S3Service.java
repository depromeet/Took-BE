package com.evenly.took.global.aws.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

	String generatePresignedUploadUrl(String fileName, String path);

	String generatePresignedViewUrl(String s3Key);

	String uploadFile(MultipartFile file, String path);

	void deleteFile(String s3Key);
}
