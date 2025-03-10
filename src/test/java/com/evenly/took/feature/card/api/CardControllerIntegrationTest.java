package com.evenly.took.feature.card.api;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.MyCardResponse;
import com.evenly.took.global.integration.JwtMockIntegrationTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class CardControllerIntegrationTest extends JwtMockIntegrationTest {

	@MockitoBean
	private CardService cardService;

	@Nested
	class 내_명함_목록_조회 {
		@Test
		void 내_명함이_여러개_있을때_모두_조회() {
			// given
			List<MyCardResponse> myCardResponses = List.of(
				new MyCardResponse(
					1L, "개발자 명함", "Evenly", Job.DEVELOPER, "백엔드 개발자",
					"Spring Boot 개발자입니다", List.of("웹", "백엔드", "Spring"),
					PreviewInfoType.PROJECT, null, "profile1.jpg"),
				new MyCardResponse(
					2L, "디자이너 명함", "ABC 회사", Job.DESIGNER, "UX/UI 디자이너",
					"사용자 경험을 디자인합니다", List.of("UX", "UI", "Figma"),
					PreviewInfoType.SNS, null, "profile2.jpg"),
				new MyCardResponse(
					3L, "프리랜서 명함", null, Job.DEVELOPER, "프론트엔드 개발자",
					"프론트엔드 프리랜서입니다", List.of("React", "Vue", "프론트엔드"),
					PreviewInfoType.CONTENT, null, "profile3.jpg")
			);
			MyCardListResponse mockResponse = new MyCardListResponse(myCardResponses);

			when(cardService.findUserCardList(eq(mockUser.getId()))).thenReturn(mockResponse);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/my")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			String responseBody = response.body().asString();

			assertThat(responseBody).contains("개발자 명함");
			assertThat(responseBody).contains("디자이너 명함");
			assertThat(responseBody).contains("프리랜서 명함");
			assertThat(responseBody).contains("DEVELOPER");
			assertThat(responseBody).contains("DESIGNER");
			assertThat(responseBody).contains("PROJECT");
			assertThat(responseBody).contains("SNS");
			assertThat(responseBody).contains("CONTENT");
			assertThat(responseBody).contains("백엔드 개발자");
			assertThat(responseBody).contains("UX/UI 디자이너");
			assertThat(responseBody).contains("프론트엔드 개발자");

			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap).containsKey("data");
			assertThat(responseMap).containsKey("status");
			assertThat(responseMap.get("status")).isEqualTo("OK");

			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> cards = (List<Map<String, Object>>)dataMap.get("cards");
			assertThat(cards).hasSize(3);

			Map<String, Object> firstCard = cards.get(0);
			assertThat(firstCard).containsKeys("id", "nickname", "organization", "job", "detailJob",
				"summary", "interestDomain", "previewInfoType", "imagePath");
		}

		@Test
		void 내_명함이_없을때_빈_목록이_반환() {
			// given
			MyCardListResponse emptyResponse = new MyCardListResponse(Collections.emptyList());
			when(cardService.findUserCardList(eq(mockUser.getId()))).thenReturn(emptyResponse);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/my")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			String responseBody = response.body().asString();

			// 응답에 빈 배열이 포함되어 있는지 확인
			assertThat(responseBody).contains("\"cards\":[]");

			// JSON 구조 확인
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap).containsKey("data");
			assertThat(responseMap).containsKey("status");

			// data 내부의 cards 배열이 비어있는지 확인
			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> cards = (List<Map<String, Object>>)dataMap.get("cards");
			assertThat(cards).isEmpty();
		}

		@Test
		void 직군별로_필터링된_명함_조회() {
			// given
			List<MyCardResponse> developerCards = List.of(
				new MyCardResponse(
					1L, "백엔드 개발자", "Evenly", Job.DEVELOPER, "백엔드 개발자",
					"Spring Boot 개발자입니다", List.of("웹", "백엔드", "Spring"),
					PreviewInfoType.PROJECT, null, "profile1.jpg"),
				new MyCardResponse(
					2L, "프론트엔드 개발자", "XYZ 회사", Job.DEVELOPER, "프론트엔드 개발자",
					"React 개발자입니다", List.of("React", "프론트엔드", "Javascript"),
					PreviewInfoType.SNS, null, "profile2.jpg")
			);
			MyCardListResponse mockResponse = new MyCardListResponse(developerCards);

			when(cardService.findUserCardList(eq(mockUser.getId()))).thenReturn(mockResponse);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/my")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			String responseBody = response.body().asString();

			assertThat(responseBody).contains("백엔드 개발자");
			assertThat(responseBody).contains("프론트엔드 개발자");
			assertThat(responseBody).contains("DEVELOPER");
			assertThat(responseBody).doesNotContain("DESIGNER");

			Map<String, Object> responseMap = response.as(Map.class);
			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> cards = (List<Map<String, Object>>)dataMap.get("cards");
			assertThat(cards).hasSize(2);

			cards.forEach(card ->
				assertThat(card.get("job")).isEqualTo("DEVELOPER")
			);
		}

		@Test
		void null_필드_응답에서_제외() {
			// given
			List<MyCardResponse> cardWithNullFields = List.of(
				new MyCardResponse(
					1L, "프리랜서 명함", null, Job.DEVELOPER, "프론트엔드 개발자",
					"프론트엔드 프리랜서입니다", List.of("React", "Vue", "프론트엔드"),
					PreviewInfoType.CONTENT, null, "profile3.jpg")
			);
			MyCardListResponse mockResponse = new MyCardListResponse(cardWithNullFields);

			when(cardService.findUserCardList(eq(mockUser.getId()))).thenReturn(mockResponse);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/my")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			String responseBody = response.body().asString();
			Map<String, Object> responseMap = response.as(Map.class);
			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> cards = (List<Map<String, Object>>)dataMap.get("cards");

			Map<String, Object> firstCard = cards.get(0);
			assertThat(firstCard).containsKeys("id", "nickname", "job", "detailJob",
				"summary", "interestDomain", "previewInfoType", "imagePath");
			assertThat(firstCard).doesNotContainKey("organization");
			assertThat(firstCard).doesNotContainKey("previewInfo");

			assertThat(firstCard.get("nickname")).isEqualTo("프리랜서 명함");
			assertThat(firstCard.get("job")).isEqualTo("DEVELOPER");
		}
	}
}
