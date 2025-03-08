package com.evenly.took.feature.auth.client.apple;

import java.util.Base64;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.client.apple.dto.response.AppleUserResponse;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.global.exception.TookException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleUserInfoProvider {

	private final ObjectMapper objectMapper;

	public AppleUserResponse fetchSignupUser(String idToken, String name) {
		// 기본 사용자 정보 추출
		AppleUserResponse baseUserInfo = parseIdToken(idToken);
		// 이름 정보 추가
		return new AppleUserResponse(baseUserInfo.id(), name, baseUserInfo.email());
	}

	public AppleUserResponse fetchLoginUser(String idToken) {
		// 이름 없이 기본 사용자 정보만 추출
		return parseIdToken(idToken);
	}

	private AppleUserResponse parseIdToken(String idToken) {
		try {
			// 1. 토큰 구조 검증
			String[] tokenParts = idToken.split("\\.");
			if (tokenParts.length != 3) {
				log.error("ID 토큰 형식이 올바르지 않음: {}", idToken);
				throw new TookException(AuthErrorCode.APPLE_INVALID_ID_TOKEN);
			}

			// 2. 페이로드 디코딩
			String payload;
			try {
				payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));
			} catch (IllegalArgumentException e) {
				log.error("ID 토큰 페이로드 디코딩 실패", e);
				throw new TookException(AuthErrorCode.APPLE_INVALID_ID_TOKEN);
			}

			// 3. JSON 파싱
			JsonNode payloadJson;
			try {
				payloadJson = objectMapper.readTree(payload);
			} catch (Exception e) {
				log.error("ID 토큰 페이로드 JSON 파싱 실패", e);
				throw new TookException(AuthErrorCode.APPLE_INVALID_ID_TOKEN);
			}

			// 4. 필수 정보 (sub) 추출
			String sub = payloadJson.has("sub") ? payloadJson.get("sub").asText() : null;
			if (sub == null) {
				log.error("ID 토큰에 sub 클레임이 없음");
				throw new TookException(AuthErrorCode.APPLE_INVALID_ID_TOKEN);
			}

			// 5. 선택적 정보 (email) 추출
			String email = payloadJson.has("email") ? payloadJson.get("email").asText() : null;

			// 6. 사용자 응답 객체 생성 및 반환 (이름 정보 없음)
			return new AppleUserResponse(sub, null, email);

		} catch (TookException e) {
			throw e;
		} catch (Exception e) {
			log.error("ID 토큰 검증 및 사용자 정보 추출 실패", e);
			throw new TookException(AuthErrorCode.APPLE_SERVER_ERROR);
		}
	}
}
