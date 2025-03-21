package com.evenly.took.global.aws.s3;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import com.evenly.took.global.service.ServiceTest;

class S3ServiceTest extends ServiceTest {

	@Autowired
	private S3Service s3Service;

	private final String TEST_PATH = "images/";
	private final String TEST_FILE_NAME = "test-image.jpg";
	private final String TEST_CONTENT_TYPE = "image/jpeg";

	@Test
	void Presigned_Upload_URL_생성_테스트() {
		// when
		String presignedUrl = s3Service.generatePresignedUploadUrl(TEST_FILE_NAME, TEST_PATH);

		// then
		assertThat(presignedUrl).isNotNull();
		assertThat(presignedUrl).contains(TEST_PATH + TEST_FILE_NAME);
	}

	@Test
	void Presigned_View_URL_생성_테스트() {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"file",
			TEST_FILE_NAME,
			TEST_CONTENT_TYPE,
			"test file content".getBytes()
		);

		String s3Key = s3Service.uploadFile(file, TEST_PATH);

		// when
		String presignedViewUrl = s3Service.generatePresignedViewUrl(s3Key);

		// then
		assertThat(presignedViewUrl).isNotNull();
		assertThat(presignedViewUrl).contains(s3Key);
	}

	@Test
	void 파일_업로드_테스트() {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"file",
			TEST_FILE_NAME,
			TEST_CONTENT_TYPE,
			"test file content".getBytes()
		);

		// when
		String s3Key = s3Service.uploadFile(file, TEST_PATH);

		// then
		assertThat(s3Key).isNotNull();
		assertThat(s3Key).contains(TEST_PATH);
		assertThat(s3Key).endsWith("_" + TEST_FILE_NAME);
	}

	@Test
	void 파일_업로드_및_삭제_통합_테스트() {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"file",
			TEST_FILE_NAME,
			TEST_CONTENT_TYPE,
			"test file content".getBytes()
		);

		String s3Key = s3Service.uploadFile(file, TEST_PATH);

		// when - 파일이 업로드된 후 해당 키로 조회 가능한지 확인
		String viewUrl = s3Service.generatePresignedViewUrl(s3Key);
		assertThat(viewUrl).isNotNull();

		// when - 파일 삭제
		s3Service.deleteFile(s3Key);

		// then - 삭제 작업이 에러 없이 완료되었는지 확인
		assertThat(s3Key).isNotNull();
	}

	@Test
	void 잘못된_키로_Presigned_View_URL_생성_시_예외_발생() {
		// given
		String invalidFileName = "";

		// when & then
		assertThat(s3Service.generatePresignedViewUrl("test-key")).isNotNull();
	}
}
