package com.evenly.took.global.aws.s3;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.evenly.took.global.aws.properties.AwsProperties;
import com.evenly.took.global.aws.s3.exception.S3ErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

	private final S3Presigner s3Presigner;
	private final S3Client s3Client;
	private final AwsProperties awsProperties;

	public String generatePresignedUploadUrl(String fileName, String path) {
		try {
			String contentType = getContentTypeFromFileName(fileName);
			String key = awsProperties.s3().env() + path + fileName;
			String bucket = awsProperties.s3().bucket();

			PutObjectRequest objectRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(contentType)
				.build();

			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(5))
				.putObjectRequest(objectRequest)
				.build();

			PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
			return presignedRequest.url().toString();
		} catch (Exception e) {
			log.error("Error generating presigned upload URL for file: {}", fileName, e);
			throw new TookException(S3ErrorCode.PRESIGNED_UPLOAD_URL_GENERATION_ERROR);
		}
	}

	public String generatePresignedViewUrl(String s3Key) {
		try {
			String bucket = awsProperties.s3().bucket();
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucket)
				.key(s3Key)
				.build();

			GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(60))
				.getObjectRequest(getObjectRequest)
				.build();

			PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
			return presignedGetObjectRequest.url().toString();
		} catch (Exception e) {
			log.error("Error generating presigned view URL for key: {}", s3Key, e);
			throw new TookException(S3ErrorCode.PRESIGNED_VIEW_URL_GENERATION_ERROR);
		}
	}

	public String uploadFile(MultipartFile file, String path) {
		String fileName = createFileName(file.getOriginalFilename());
		String key = awsProperties.s3().env() + path + fileName;

		if (fileName.contains("default") || file.isEmpty()) {
			key = "base_image.png";
		}

		String bucket = awsProperties.s3().bucket();
		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(file.getContentType())
				.build();

			s3Client.putObject(putObjectRequest,
				RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			return key;
		} catch (IOException e) {
			log.error("Error uploading file: {}", fileName, e);
			throw new TookException(S3ErrorCode.FILE_UPLOAD_ERROR);
		}
	}

	public void deleteFile(String s3Key) {
		String bucket = awsProperties.s3().bucket();
		try {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(s3Key)
				.build();

			s3Client.deleteObject(deleteObjectRequest);
		} catch (Exception e) {
			log.error("Error deleting file with key: {}", s3Key, e);
			throw new TookException(S3ErrorCode.FILE_DELETE_ERROR);
		}
	}

	private String createFileName(String originalFileName) {
		return UUID.randomUUID().toString() + "_" + originalFileName;
	}

	private String getContentTypeFromFileName(String fileName) {
		Map<String, String> contentTypeMap = new HashMap<>();
		contentTypeMap.put("jpg", "image/jpeg");
		contentTypeMap.put("jpeg", "image/jpeg");
		contentTypeMap.put("png", "image/png");
		contentTypeMap.put("gif", "image/gif");
		contentTypeMap.put("pdf", "application/pdf");
		contentTypeMap.put("txt", "text/plain");
		contentTypeMap.put("html", "text/html");
		contentTypeMap.put("json", "application/json");
		// 필요한 파일타입 추가

		String extension = getFileExtension(fileName);
		return contentTypeMap.getOrDefault(extension, "application/octet-stream");
	}

	private String getFileExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf(".");
		return (lastIndexOfDot == -1) ? "" : fileName.substring(lastIndexOfDot + 1).toLowerCase();
	}
}
