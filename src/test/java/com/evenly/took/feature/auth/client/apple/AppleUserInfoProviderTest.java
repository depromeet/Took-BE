package com.evenly.took.feature.auth.client.apple;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.evenly.took.feature.auth.client.apple.dto.response.AppleUserResponse;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.MockTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

class AppleUserInfoProviderTest extends MockTest {

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private AppleUserInfoProvider appleUserInfoProvider;

	private static final String VALID_ID_TOKEN = createValidIdToken();

	@Nested
	class 애플_ID_토큰_테스트 {

		@Test
		void ID_토큰_파싱_성공() throws Exception {
			// Arrange
			String idToken = createValidIdToken();
			String payload = decodeBase64UrlPayload(idToken);

			// ObjectMapper로 실제 JSON 파싱
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode payloadJson = objectMapper.readTree(payload);

			// Mock 설정 대신 실제 파싱된 JSON 사용
			assertNotNull(payloadJson);
			assertTrue(payloadJson.has("sub"));
			assertEquals("apple_user_id", payloadJson.get("sub").asText());
			assertTrue(payloadJson.has("email"));
			assertEquals("user@example.com", payloadJson.get("email").asText());
		}

		@Test
		void 최초_회원가입_사용자_이름_포함() throws Exception {
			// Arrange
			String name = "테스트";
			ObjectNode mockPayload = mock(ObjectNode.class);
			when(mockPayload.has("sub")).thenReturn(true);
			when(mockPayload.get("sub")).thenReturn(new TextNode("apple_user_id"));

			when(mockPayload.has("email")).thenReturn(true);
			when(mockPayload.get("email")).thenReturn(new TextNode("user@example.com"));

			when(objectMapper.readTree(anyString())).thenReturn(mockPayload);

			// Act
			AppleUserResponse response = appleUserInfoProvider.fetchSignupUser(VALID_ID_TOKEN, name);

			// Assert
			assertNotNull(response);
			assertEquals("apple_user_id", response.id());
			assertEquals("user@example.com", response.email());
			assertEquals(name, response.name());
		}
	}

	@Nested
	class 오류_시나리오_테스트 {

		@Test
		void 잘못된_ID_토큰_구조() {
			// Arrange
			String invalidToken = "invalid.token";

			// Act & Assert
			TookException exception = assertThrows(TookException.class, () -> {
				appleUserInfoProvider.fetchLoginUser(invalidToken);
			});

			assertEquals(AuthErrorCode.APPLE_INVALID_ID_TOKEN, exception.getErrorCode());
		}

		@Test
		void 토큰_필수_정보_sub_누락() throws Exception {
			// Arrange
			ObjectNode mockPayload = mock(ObjectNode.class);
			when(mockPayload.has("sub")).thenReturn(false);

			when(objectMapper.readTree(anyString())).thenReturn(mockPayload);

			// Act & Assert
			TookException exception = assertThrows(TookException.class, () -> {
				appleUserInfoProvider.fetchLoginUser(VALID_ID_TOKEN);
			});

			assertEquals(AuthErrorCode.APPLE_INVALID_ID_TOKEN, exception.getErrorCode());
		}

		@Test
		void JSON_파싱_오류() throws Exception {
			// Arrange
			when(objectMapper.readTree(anyString())).thenThrow(new RuntimeException("JSON 파싱 실패"));

			// Act & Assert
			TookException exception = assertThrows(TookException.class, () -> {
				appleUserInfoProvider.fetchLoginUser(VALID_ID_TOKEN);
			});

			assertEquals(AuthErrorCode.APPLE_INVALID_ID_TOKEN, exception.getErrorCode());
		}
	}

	private String decodeBase64UrlPayload(String idToken) {
		String[] parts = idToken.split("\\.");
		if (parts.length < 2) {
			throw new IllegalArgumentException("Invalid ID token");
		}
		return new String(Base64.getUrlDecoder().decode(parts[1]));
	}

	private static String createValidIdToken() {
		try {
			// 헤더
			String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"ES256\",\"kid\":\"test\"}".getBytes());

			// 페이로드
			String payload = Base64.getUrlEncoder()
				.encodeToString("{\"sub\":\"apple_user_id\",\"email\":\"user@example.com\"}".getBytes());

			// 시그니처
			String signature = Base64.getUrlEncoder().encodeToString("dummy_signature".getBytes());

			return header + "." + payload + "." + signature;
		} catch (Exception e) {
			throw new RuntimeException("Test ID 토큰 생성 실패", e);
		}
	}
}
