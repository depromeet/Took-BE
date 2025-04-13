package com.evenly.took.feature.auth.client.apple;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.apple.error.AppleTokenProviderErrorHandler;
import com.evenly.took.feature.auth.config.properties.AppleProperties;
import com.evenly.took.feature.auth.config.properties.AppleUrlProperties;
import com.evenly.took.global.service.MockTest;

class AppleTokenProviderTest extends MockTest {

	@Mock
	private AppleTokenProviderErrorHandler errorHandler;

	@Nested
	class 클라이언트_생성_테스트 {

		@Test
		void 클라이언트_시크릿_정상_생성() throws Exception {
			// Arrange
			AppleProperties mockProps = mock(AppleProperties.class);
			when(mockProps.keyId()).thenReturn("test_key_id");
			when(mockProps.teamId()).thenReturn("test_team_id");
			when(mockProps.clientId()).thenReturn("test_client_id");
			when(mockProps.privateKeyPath()).thenReturn(createTempPrivateKeyFile());

			AppleUrlProperties mockUrlProps = mock(AppleUrlProperties.class);

			// Mock RestClient.Builder 생성
			RestClient.Builder mockBuilder = mock(RestClient.Builder.class);
			RestClient mockClient = mock(RestClient.class);

			when(mockBuilder.clone())
				.thenReturn(mockBuilder);
			when(mockBuilder.defaultStatusHandler(any(AppleTokenProviderErrorHandler.class)))
				.thenReturn(mockBuilder);
			when(mockBuilder.defaultHeader(
				eq(HttpHeaders.CONTENT_TYPE),
				eq(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
				eq(StandardCharsets.UTF_8.name())
			)).thenReturn(mockBuilder);
			when(mockBuilder.build()).thenReturn(mockClient);

			AppleTokenProvider appleTokenProvider = new AppleTokenProvider(
				mockBuilder,
				errorHandler,
				mockProps,
				mockUrlProps
			);

			// Act
			String clientSecret = invokePrivateMethod(appleTokenProvider, "generateClientSecret");

			// Assert
			assertNotNull(clientSecret);

			// JWT 구조 검증 (3개의 점으로 구분된 부분)
			String[] parts = clientSecret.split("\\.");
			assertEquals(3, parts.length, "클라이언트 시크릿은 유효한 JWT여야 합니다");
		}

		@Test
		void 개인키_파싱_테스트() throws Exception {
			// Arrange
			String tempFilePath = createTempPrivateKeyFile();

			AppleProperties mockProps = mock(AppleProperties.class);
			when(mockProps.privateKeyPath()).thenReturn(tempFilePath);

			AppleUrlProperties mockUrlProps = mock(AppleUrlProperties.class);

			RestClient.Builder mockBuilder = mock(RestClient.Builder.class);
			RestClient mockClient = mock(RestClient.class);

			// 체이닝을 위한 모킹 설정
			when(mockBuilder.clone())
				.thenReturn(mockBuilder);
			when(mockBuilder.defaultStatusHandler(any(AppleTokenProviderErrorHandler.class)))
				.thenReturn(mockBuilder);
			when(mockBuilder.defaultHeader(
				eq(HttpHeaders.CONTENT_TYPE),
				eq(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
				eq(StandardCharsets.UTF_8.name())
			)).thenReturn(mockBuilder);
			when(mockBuilder.build()).thenReturn(mockClient);

			AppleTokenProvider appleTokenProvider = new AppleTokenProvider(
				mockBuilder,
				errorHandler,
				mockProps,
				mockUrlProps
			);

			// Act
			PrivateKey privateKey = invokePrivateMethod(appleTokenProvider, "getPrivateKey");

			// Assert
			assertNotNull(privateKey);
			assertEquals("EC", privateKey.getAlgorithm(), "개인키는 EC 알고리즘이어야 합니다");
		}
	}

	// 테스트용 개인키 생성 유틸리티 메서드
	private String generateMockPrivateKey() {
		try {
			java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("EC");
			keyGen.initialize(256);
			java.security.PrivateKey privateKey = keyGen.generateKeyPair().getPrivate();

			byte[] encodedPrivateKey = privateKey.getEncoded();
			String base64PrivateKey = Base64.getEncoder().encodeToString(encodedPrivateKey);

			return "-----BEGIN PRIVATE KEY-----\n" +
				base64PrivateKey +
				"\n-----END PRIVATE KEY-----";
		} catch (Exception e) {
			throw new RuntimeException("테스트용 개인키 생성 실패", e);
		}
	}

	private String createTempPrivateKeyFile() throws Exception {
		String privateKeyContent = generateMockPrivateKey();
		java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("apple_test_key", ".p8");
		java.nio.file.Files.write(tempFile, privateKeyContent.getBytes());
		return tempFile.toString();
	}

	// 프라이빗 메서드 호출을 위한 유틸 메서드
	@SuppressWarnings("unchecked")
	private <T> T invokePrivateMethod(Object obj, String methodName) throws Exception {
		java.lang.reflect.Method method = obj.getClass().getDeclaredMethod(methodName);
		method.setAccessible(true);
		return (T)method.invoke(obj);
	}
}
