package com.evenly.took.feature.card.api;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.SNSType;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.domain.vo.SNS;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.mapper.CardMapper;
import com.evenly.took.global.domain.TestCardFactory;
import com.evenly.took.global.integration.JwtMockIntegrationTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class CardIntegrationTest extends JwtMockIntegrationTest {

	@MockitoBean
	private CardService cardService;

	@Autowired
	private CardMapper cardMapper;

	@Nested
	class 전체_커리어_조회 {

		@Test
		void 직군에_따른_전체_커리어를_조회한다() {
			given().log().all()
				.when().get("/api/card/register?job=DEVELOPER")
				.then().log().all()
				.statusCode(200);
		}

		@Test
		void 제공하지_않는_직군의_커리어를_요청한_경우_400_예외를_반환한다() {
			given().log().all()
				.when().get("/api/card/register?job=INVALID")
				.then().log().all()
				.statusCode(400);
		}
	}

	@Nested
	class 명함_상세_조회 {
		@Test
		void 명함_상세_정보를_조회하면_빈_배열은_응답에_포함되지_않음() {
			// given
			Long cardId = 1L;

			CardDetailResponse mockResponse = cardMapper.toCardDetailResponse(
				TestCardFactory.createCard(builder -> {
					builder.nickname("홍길동");
					builder.summary("한줄 소개입니다.");
					builder.organization("ABC 회사");
					builder.region("서울 강남구");
					builder.interestDomain(List.of("웹", "모바일"));
					builder.news("최근 소식입니다.");
					builder.hobby("등산, 독서");
					builder.sns(new ArrayList<>());
					builder.content(new ArrayList<>());
					builder.project(new ArrayList<>());
				})
			);

			when(cardService.findCardDetail(eq(mockUser.getId()), any(CardDetailRequest.class)))
				.thenReturn(mockResponse);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.param("cardId", cardId)
				.when()
				.get("/api/card/detail")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			String responseBody = response.body().asString();

			assertThat(responseBody).doesNotContain("\"content\":[]");
			assertThat(responseBody).doesNotContain("\"project\":[]");
			assertThat(responseBody).doesNotContain("\"sns\":[]");

			assertThat(responseBody).contains("\"nickname\"");
			assertThat(responseBody).contains("\"summary\"");

			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("OK");
		}

		@Test
		void 명함_상세_정보_조회시_데이터가_있는_배열은_응답에_포함() {
			// given
			Long cardId = 1L;

			Card card = TestCardFactory.createCard(builder -> {
				builder.nickname("홍길동");
				builder.summary("한줄 소개입니다.");
				builder.organization("ABC 회사");
				builder.region("서울 강남구");
				builder.interestDomain(List.of("웹", "모바일"));
				builder.news("최근 소식입니다.");
				builder.hobby("등산, 독서");

				builder.sns(Collections.singletonList(
					new SNS(SNSType.GITHUB, "https://github.com/user")
				));
				builder.content(Collections.singletonList(
					new Content("블로그 글", "https://blog.com", "image.jpg", "설명")
				));
				builder.project(Collections.singletonList(
					new Project("프로젝트", "https://project.com", "image.jpg", "설명")
				));
			});

			CardDetailResponse mockResponse = cardMapper.toCardDetailResponse(card);

			when(cardService.findCardDetail(eq(mockUser.getId()), any(CardDetailRequest.class)))
				.thenReturn(mockResponse);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.param("cardId", cardId)
				.when()
				.get("/api/card/detail")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			String responseBody = response.body().asString();

			assertThat(responseBody).contains("\"interestDomain\"");
			assertThat(responseBody).contains("\"content\"");
			assertThat(responseBody).contains("\"project\"");
			assertThat(responseBody).contains("\"sns\"");

			assertThat(responseBody).contains("\"nickname\":\"홍길동\"");
			assertThat(responseBody).contains("\"블로그 글\"");
			assertThat(responseBody).contains("\"프로젝트\"");

			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap).containsKey("data");
			assertThat(responseMap).containsKey("status");
		}
	}
}
