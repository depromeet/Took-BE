package com.evenly.took.feature.card.api;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.domain.SNSType;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.domain.vo.SNS;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.MyCardResponse;
import com.evenly.took.feature.card.mapper.CardMapper;
import com.evenly.took.global.aws.s3.S3Service;
import com.evenly.took.global.config.testcontainers.S3TestConfig;
import com.evenly.took.global.domain.TestCardFactory;
import com.evenly.took.global.integration.JwtMockIntegrationTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@Import(S3TestConfig.class)
public class CardIntegrationTest extends JwtMockIntegrationTest {

	@MockitoBean
	private CardService cardService;

	@Autowired
	private CardMapper cardMapper;

	@MockitoBean
	CardRepository cardRepository;

	@MockitoBean
	private S3Service s3Service;

	@Nested
	class 명함_생성 {

		private MockMultipartFile testImageFile;

		@BeforeEach
		void setUp() {
			// Create a mock image file with a unique name to avoid conflicts
			String fileName = "test-image-" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";

			try {
				// Use a simple byte array to avoid file system operations
				byte[] imageContent = "test image content".getBytes();

				testImageFile = new MockMultipartFile(
					"profileImage",
					fileName,
					"image/jpeg",
					imageContent
				);

				// Instead of trying to upload the file to S3, let's mock the S3Service
				// Most controllers will use this service internally, so we don't need to upload here

				// Mock the card repository for non-limit test cases
				BDDMockito.given(cardRepository.countByUserIdAndDeletedAtIsNull(anyLong()))
					.willReturn(1L);

			} catch (Exception e) {
				System.err.println("Error setting up test: " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Test
		void 명함_생성에_성공한다() {

			// When & Then
			try {
				given().log().all()
					.header("Authorization", "Bearer %s".formatted(authToken))
					.contentType("multipart/form-data")
					.multiPart("profileImage", testImageFile.getOriginalFilename(),
						testImageFile.getBytes(), testImageFile.getContentType())
					.multiPart("nickname", "윤장원")
					.multiPart("detailJobId", "1")
					.multiPart("interestDomain", "[\"웹\", \"모바일\", \"클라우드\"]")
					.multiPart("summary", "백엔드 개발을 좋아하는 개발자입니다")
					.multiPart("organization", "ABC 회사")
					.multiPart("sns", "[{\"type\":\"LINKEDIN\",\"link\":\"https://linkedin.com/in/username\"}]")
					.multiPart("region", "서울 강남구")
					.multiPart("hobby", "등산, 독서")
					.multiPart("news", "최근 블로그 포스팅 시작했습니다")
					.multiPart("content",
						"[{\"type\":\"project\",\"title\":\"Took-BE\",\"link\":\"https://github.com/depromeet/Took-BE\",\"imageUrl\":\"https://opengraph.githubassets.com/image.jpg\",\"description\":\"Server 레포입니다.\"}]")
					.multiPart("project",
						"[{\"type\":\"project\",\"title\":\"Took-BE\",\"link\":\"https://github.com/depromeet/Took-BE\",\"imageUrl\":\"https://opengraph.githubassets.com/image.jpg\",\"description\":\"Server 레포입니다.\"}]")
					.multiPart("previewInfoType", "SNS")
					.when().post("/api/card")
					.then().log().all()
					.statusCode(201);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Test
		void 필수_필드가_누락된_경우_예외를_반환한다() {
			try {
				given().log().all()
					.header("Authorization", "Bearer %s".formatted(authToken))
					.contentType("multipart/form-data")
					.multiPart("profileImage", testImageFile.getOriginalFilename(),
						testImageFile.getBytes(), testImageFile.getContentType())
					// nickname is missing
					.multiPart("detailJobId", "1")
					.multiPart("interestDomain", "[\"웹\", \"모바일\", \"클라우드\"]")
					.multiPart("summary", "백엔드 개발을 좋아하는 개발자입니다")
					.when().post("/api/card")
					.then().log().all()
					.statusCode(400);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Test
		void 프로필_이미지가_없는_경우_예외를_반환한다() {
			// When & Then
			given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType("multipart/form-data")
				// .multiPart("profileImage", )
				.multiPart("nickname", "윤장원")
				.multiPart("detailJobId", "1")
				.multiPart("interestDomain", "[\"웹\", \"모바일\", \"클라우드\"]")
				.multiPart("summary", "백엔드 개발을 좋아하는 개발자입니다")
				.when().post("/api/card")
				.then().log().all()
				.statusCode(400);
		}

		@Test
		void 인증되지_않은_사용자가_명함_생성을_요청하면_401_예외를_반환한다() {
			// When & Then
			try {
				given().log().all()
					.contentType("multipart/form-data")
					.multiPart("profileImage", testImageFile.getOriginalFilename(),
						testImageFile.getInputStream(), testImageFile.getContentType())
					.multiPart("nickname", "윤장원")
					.multiPart("detailJobId", "1")
					.multiPart("interestDomain", "[\"웹\", \"모바일\", \"클라우드\"]")
					.multiPart("summary", "백엔드 개발을 좋아하는 개발자입니다")
					.when().post("/api/card")
					.then().log().all()
					.statusCode(401);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

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
	}

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
