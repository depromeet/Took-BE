package com.evenly.took.feature.card.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.card.application.LinkExtractor;
import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.card.dto.request.AddFolderRequest;
import com.evenly.took.feature.card.dto.request.FixFolderRequest;
import com.evenly.took.feature.card.dto.request.FixReceivedCardRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.request.ReceiveCardRequest;
import com.evenly.took.feature.card.dto.request.RemoveFolderRequest;
import com.evenly.took.feature.card.dto.request.RemoveReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.SetReceivedCardsFolderRequest;
import com.evenly.took.feature.card.dto.response.CardResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.feature.card.exception.FolderErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.aws.s3.S3Service;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.integration.JwtMockIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class CardIntegrationTest extends JwtMockIntegrationTest {

	@MockitoBean
	LinkExtractor linkExtractor;

	@Autowired
	S3Service s3Service;

	@Autowired
	CardRepository cardRepository;

	@Nested
	class 내_명함_목록_조회 {

		@Test
		void 내_명함이_여러개_있을때_모두_조회() {
			// given
			Card card1 = cardFixture.creator()
				.user(mockUser)
				.nickname("닉네임1")
				.previewInfo(PreviewInfoType.PROJECT)
				.create();
			Card card2 = cardFixture.creator()
				.user(mockUser)
				.nickname("닉네임2")
				.previewInfo(PreviewInfoType.SNS)
				.create();
			Card card3 = cardFixture.creator()
				.user(mockUser)
				.nickname("닉네임3")
				.previewInfo(PreviewInfoType.CONTENT)
				.create();

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

			assertThat(responseBody).contains(card1.getNickname());
			assertThat(responseBody).contains(card2.getNickname());
			assertThat(responseBody).contains(card3.getNickname());
			assertThat(responseBody).contains(card1.getCareer().getJob().name());
			assertThat(responseBody).contains(card2.getCareer().getJob().name());
			assertThat(responseBody).contains(card3.getCareer().getJob().name());
			assertThat(responseBody).contains("PROJECT");
			assertThat(responseBody).contains("SNS");
			assertThat(responseBody).contains("CONTENT");

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
			// given, when
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
			Card card1 = cardFixture.creator()
				.user(mockUser)
				.nickname("닉네임1")
				.create();
			Card card2 = cardFixture.creator()
				.user(mockUser)
				.nickname("닉네임2")
				.create();

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

			assertThat(responseBody).contains(card1.getCareer().getJob().name());
			assertThat(responseBody).contains(card2.getCareer().getJob().name());
			assertThat(responseBody).contains(card1.getCareer().getDetailJobEn());
			assertThat(responseBody).contains(card2.getCareer().getDetailJobEn());

			Map<String, Object> responseMap = response.as(Map.class);
			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> cards = (List<Map<String, Object>>)dataMap.get("cards");
			assertThat(cards).hasSize(2);
		}

		@Test
		void null_필드_응답에서_제외() {
			// given
			Card card = cardFixture.creator()
				.user(mockUser)
				.nickname("닉네임")
				.organization(null)
				.region(null)
				.create();

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
			assertThat(firstCard).doesNotContainKey("region");

			assertThat(firstCard.get("nickname")).isEqualTo(card.getNickname());
			assertThat(firstCard.get("job")).isEqualTo(card.getCareer().getJob().name());
		}
	}

	@Nested
	class 명함_상세_조회 {

		@Test
		void 명함_상세_정보를_조회하면_빈_배열은_응답에_포함되지_않음() {
			// given
			Card card = cardFixture.creator()
				.user(mockUser)
				.contents(List.of())
				.projects(List.of())
				.sns(List.of())
				.create();

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.param("cardId", card.getId())
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
			Card card = cardFixture.creator()
				.user(mockUser)
				.create();

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.param("cardId", card.getId())
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
			assertThat(responseBody).contains("\"imagePath\"");

			assertThat(responseBody).contains("\"nickname\":\"닉네임\"");
			assertThat(responseBody).contains("\"제목\"");
			assertThat(responseBody).contains("\"링크\"");

			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap).containsKey("data");
			assertThat(responseMap).containsKey("status");
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

	@Disabled
	@Nested
	class 링크_스크랩 {

		@Test
		void 링크를_스크랩하여_정보를_제공한다() {
			BDDMockito.given(linkExtractor.extractLink(anyString()))
				.willReturn(new CrawledDto("title", "link", "imageUrl", "description"));

			LinkRequest request = new LinkRequest("https://github.com/depromeet/Took-BE");
			ScrapResponse response = given().log().all()
				.header("Authorization", authToken)
				.contentType(ContentType.JSON)
				.body(request)
				.when().post("/api/card/scrap")
				.then().log().all()
				.statusCode(200)
				.extract()
				.jsonPath()
				.getObject("data", ScrapResponse.class);

			assertAll(
				() -> verify(linkExtractor, times(1)).extractLink(anyString()),
				() -> assertThat(response.title()).isEqualTo("title"),
				() -> assertThat(response.link()).isEqualTo("link"),
				() -> assertThat(response.imageUrl()).isEqualTo("imageUrl"),
				() -> assertThat(response.description()).isEqualTo("description")
			);
		}

		@Test
		void 빈_링크의_경우_예외를_반환한다() {
			LinkRequest request = new LinkRequest("        ");
			List<String> errorMessages = given().log().all()
				.header("Authorization", authToken)
				.contentType(ContentType.JSON)
				.body(request)
				.when().post("/api/card/scrap")
				.then().log().all()
				.statusCode(400)
				.extract()
				.jsonPath()
				.getList("errors.message", String.class);

			assertThat(errorMessages.get(0)).isEqualTo("유효하지 않은 링크입니다.");
		}

		@Test
		void 크롤링_불가능한_링크의_경우_예외를_반환한다() {
			BDDMockito.given(linkExtractor.extractLink(anyString()))
				.willThrow(new TookException(CardErrorCode.CANNOT_CRAWL));

			LinkRequest request = new LinkRequest("invalid_link");
			String errorMessage = given().log().all()
				.header("Authorization", authToken)
				.contentType(ContentType.JSON)
				.body(request)
				.when().post("/api/card/scrap")
				.then().log().all()
				.statusCode(400)
				.extract()
				.jsonPath()
				.getObject("message", String.class);

			assertThat(errorMessage).isEqualTo("크롤링에 실패하였습니다.");
		}
	}

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
			} catch (Exception e) {
				System.err.println("Error setting up test: " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Test
		void 명함_생성에_성공한다() throws IOException {

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
		}

		@Test
		void 필수_필드가_누락된_경우_예외를_반환한다() throws IOException {
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
		}

		@Test
		void 프로필_이미지가_없는_경우_예외를_반환한다() {
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
		void 인증되지_않은_사용자가_명함_생성을_요청하면_401_예외를_반환한다() throws IOException {
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
		}
	}

	@Nested
	class 폴더_생성 {

		@Test
		void 폴더_생성_성공() {
			// given
			AddFolderRequest request = new AddFolderRequest("업무 관련");

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.post("/api/card/folder")
				.then()
				.statusCode(HttpStatus.CREATED.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("CREATED");
			assertThat(responseMap.get("message")).isEqualTo("폴더 생성 성공");
		}

		@Test
		void 인증되지_않은_사용자가_폴더_생성_요청시_401_예외를_반환한다() {
			// given
			AddFolderRequest request = new AddFolderRequest("업무 관련");

			// when & then
			given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(request)
				.when()
				.post("/api/card/folder")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
		}

		@Test
		void 폴더명이_비어있을_경우_400_예외를_반환한다() {
			// given
			AddFolderRequest request = new AddFolderRequest("");

			// when & then
			given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.post("/api/card/folder")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
		}
	}

	@Nested
	class 폴더_목록_조회 {

		@Test
		void 폴더가_여러개_있을때_모두_조회() {
			// given
			Folder folder1 = folderFixture.creator()
				.user(mockUser)
				.name("업무 관련")
				.create();

			Folder folder2 = folderFixture.creator()
				.user(mockUser)
				.name("개인 네트워크")
				.create();

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/folders")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("OK");

			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> folders = (List<Map<String, Object>>)dataMap.get("folders");

			assertThat(folders).hasSize(2);
			assertThat(folders.get(0)).containsKeys("id", "name");

			List<String> folderNames = folders.stream()
				.map(folder -> (String)folder.get("name"))
				.toList();

			assertThat(folderNames).containsExactlyInAnyOrder("업무 관련", "개인 네트워크");
		}

		@Test
		void 폴더가_없을때_빈_목록_반환() {
			// given, when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/folders")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> folders = (List<Map<String, Object>>)dataMap.get("folders");

			assertThat(folders).isEmpty();
		}

		@Test
		void 인증되지_않은_사용자_폴더_목록_조회_요청시_401_예외를_반환한다() {
			// when & then
			given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.get("/api/card/folders")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
		}
	}

	@Nested
	class 폴더_이름_변경 {

		@Test
		void 폴더_이름_변경_성공() {
			// given
			Folder folder = folderFixture.creator()
				.user(mockUser)
				.name("원래 이름")
				.create();

			FixFolderRequest request = new FixFolderRequest(folder.getId(), "변경된 이름");

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.put("/api/card/folder")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("OK");
			assertThat(responseMap.get("message")).isEqualTo("폴더 이름 변경 성공");

			// Verify the name is changed via API response
			ExtractableResponse<Response> folderListResponse = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/folders")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			Map<String, Object> listResponseMap = folderListResponse.as(Map.class);
			Map<String, Object> listDataMap = (Map<String, Object>)listResponseMap.get("data");
			List<Map<String, Object>> folders = (List<Map<String, Object>>)listDataMap.get("folders");

			Map<String, Object> updatedFolder = folders.stream()
				.filter(f -> ((Integer)f.get("id")) == folder.getId().intValue())
				.findFirst()
				.orElseThrow();

			assertThat(updatedFolder.get("name")).isEqualTo("변경된 이름");
		}

		@Test
		void 존재하지_않는_폴더_이름_변경시_404_예외를_반환한다() {
			// given
			FixFolderRequest request = new FixFolderRequest(9999L, "변경된 이름");

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.put("/api/card/folder")
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(FolderErrorCode.FOLDER_NOT_FOUND.getMessage());
		}

		@Test
		void 다른_사용자의_폴더_이름_변경시_403_예외를_반환한다() {
			// given
			User other = userFixture.creator()
				.name("다른 사용자")
				.create();
			Folder folder = folderFixture.creator()
				.user(other)
				.name("다른 사용자의 폴더")
				.create();

			FixFolderRequest request = new FixFolderRequest(folder.getId(), "변경 시도");

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.put("/api/card/folder")
				.then()
				.statusCode(HttpStatus.FORBIDDEN.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(FolderErrorCode.FOLDER_ACCESS_DENIED.getMessage());
		}
	}

	@Nested
	class 폴더_제거 {

		@Test
		void 폴더_제거_성공() {
			// given
			Folder folder = folderFixture.creator()
				.user(mockUser)
				.name("삭제할 폴더")
				.create();

			RemoveFolderRequest request = new RemoveFolderRequest(folder.getId());

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.delete("/api/card/folder")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("OK");
			assertThat(responseMap.get("message")).isEqualTo("폴더 제거 성공");

			// Verify the folder is soft deleted in the database
			ExtractableResponse<Response> folderListResponse = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/folders")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			Map<String, Object> listResponseMap = folderListResponse.as(Map.class);
			Map<String, Object> listDataMap = (Map<String, Object>)listResponseMap.get("data");
			List<Map<String, Object>> folders = (List<Map<String, Object>>)listDataMap.get("folders");

			// Check that the deleted folder is not in the list
			boolean folderExists = folders.stream()
				.anyMatch(f -> ((Integer)f.get("id")) == folder.getId().intValue());

			assertThat(folderExists).isFalse();
		}

		@Test
		void 존재하지_않는_폴더_제거시_404_예외를_반환한다() {
			// given
			RemoveFolderRequest request = new RemoveFolderRequest(9999L);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.delete("/api/card/folder")
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(FolderErrorCode.FOLDER_NOT_FOUND.getMessage());
		}

		@Test
		void 다른_사용자의_폴더_제거시_403_예외를_반환한다() {
			// given
			User other = userFixture.creator()
				.name("다른 사용자")
				.create();
			Folder folder = folderFixture.creator()
				.user(other)
				.name("다른 사용자의 폴더")
				.create();

			RemoveFolderRequest request = new RemoveFolderRequest(folder.getId());

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.delete("/api/card/folder")
				.then()
				.statusCode(HttpStatus.FORBIDDEN.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(FolderErrorCode.FOLDER_ACCESS_DENIED.getMessage());
		}
	}

	@Nested
	class 명함_수신 {

		@Test
		void 명함_수신_성공() {
			// given
			User cardOwner = userFixture.creator()
				.name("명함소유자")
				.create();
			Card card = cardFixture.creator()
				.user(cardOwner)
				.create();
			ReceiveCardRequest request = new ReceiveCardRequest(card.getId());

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.post("/api/card/receive")
				.then()
				.statusCode(HttpStatus.CREATED.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("CREATED");
			assertThat(responseMap.get("message")).isEqualTo("명함 수신 성공");
		}

		@Test
		void 존재하지_않는_명함_수신시_404_예외를_반환한다() {
			// given
			ReceiveCardRequest request = new ReceiveCardRequest(9999L);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.post("/api/card/receive")
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(CardErrorCode.CARD_NOT_FOUND.getMessage());
		}

		@Test
		void 자신의_명함_수신시_400_예외를_반환한다() {
			// given
			Card card = cardFixture.creator()
				.user(mockUser)
				.create();
			ReceiveCardRequest request = new ReceiveCardRequest(card.getId());

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)  // Same user as card owner
				.body(request)
				.when()
				.post("/api/card/receive")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(CardErrorCode.CANNOT_RECEIVE_OWN_CARD.getMessage());
		}

		@Test
		void 이미_수신한_명함_다시_수신시_400_예외를_반환한다() {
			User cardOwner = userFixture.creator()
				.name("명함소유자")
				.create();
			Card card = cardFixture.creator()
				.user(cardOwner)
				.create();
			receivedCardFixture.creator()
				.user(mockUser)
				.card(card)
				.create();
			ReceiveCardRequest request = new ReceiveCardRequest(card.getId());

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.post("/api/card/receive")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(CardErrorCode.ALREADY_RECEIVED_CARD.getMessage());
		}
	}

	@Nested
	class 받은_명함_폴더_설정 {

		@Test
		void 받은_명함을_폴더에_저장_성공() {
			// given
			User cardOwner = userFixture.creator()
				.name("명함소유자")
				.create();
			Card card = cardFixture.creator()
				.user(cardOwner)
				.create();
			Folder folder = folderFixture.creator()
				.user(mockUser)
				.name("폴더")
				.create();
			receivedCardFixture.creator()
				.user(mockUser)
				.card(card)
				.create();
			SetReceivedCardsFolderRequest request = new SetReceivedCardsFolderRequest(
				folder.getId(),
				List.of(card.getId())
			);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.put("/api/card/receive/folder")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("OK");
			assertThat(responseMap.get("message")).isEqualTo("명함 폴더 설정 성공");
		}

		@Test
		void 존재하지_않는_폴더에_명함_추가시_404_예외를_반환한다() {
			// given
			User cardOwner = userFixture.creator()
				.name("명함소유자")
				.create();
			Card card = cardFixture.creator()
				.user(cardOwner)
				.create();
			receivedCardFixture.creator()
				.user(mockUser)
				.card(card)
				.create();
			SetReceivedCardsFolderRequest request = new SetReceivedCardsFolderRequest(
				9999L,
				List.of(card.getId())
			);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.put("/api/card/receive/folder")
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(FolderErrorCode.FOLDER_NOT_FOUND.getMessage());
		}
	}

	@Nested
	class 받은_명함_목록_조회 {

		@Test
		void 받은_명함_목록_조회_성공() {
			// given
			User cardOwner = userFixture.creator()
				.name("명함소유자")
				.create();
			Card card = cardFixture.creator()
				.user(cardOwner)
				.create();
			receivedCardFixture.creator()
				.user(mockUser)
				.card(card)
				.create();

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/receive")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("OK");

			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> cards = (List<Map<String, Object>>)dataMap.get("cards");

			assertThat(cards).hasSize(1);
			assertThat(cards.get(0).get("nickname")).isEqualTo(card.getNickname());
		}

		@Test
		void 특정_폴더의_받은_명함_목록_조회() {
			// given
			User cardOwner = userFixture.creator()
				.name("명함소유자")
				.create();
			Card card = cardFixture.creator()
				.user(cardOwner)
				.create();
			Folder folder = folderFixture.creator()
				.user(mockUser)
				.name("폴더")
				.create();
			ReceivedCard receivedCard = receivedCardFixture.creator()
				.user(mockUser)
				.card(card)
				.create();
			receivedCardFolderFixture.creator()
				.folder(folder)
				.receivedCard(receivedCard)
				.create();

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.queryParam("folderId", folder.getId())
				.when()
				.get("/api/card/receive")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			Map<String, Object> dataMap = (Map<String, Object>)responseMap.get("data");
			List<Map<String, Object>> cards = (List<Map<String, Object>>)dataMap.get("cards");

			assertThat(cards).hasSize(1);
			assertThat(cards.get(0).get("nickname")).isEqualTo(card.getNickname());
		}
	}

	@Nested
	class 받은_명함_삭제 {

		@Test
		void 받은_명함_삭제_성공() {
			// given
			User cardOwner = userFixture.creator()
				.name("명함소유자")
				.create();
			Card card = cardFixture.creator()
				.user(cardOwner)
				.create();
			ReceivedCard receivedCard = receivedCardFixture.creator()
				.user(mockUser)
				.card(card)
				.create();
			Folder folder = folderFixture.creator()
				.user(mockUser)
				.create();
			receivedCardFolderFixture.creator()
				.receivedCard(receivedCard)
				.folder(folder)
				.create();
			RemoveReceivedCardsRequest request = new RemoveReceivedCardsRequest(
				List.of(card.getId())
			);

			// when
			given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.delete("/api/card/receive")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			List<CardResponse> cards = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.queryParam("folderId", folder.getId())
				.when()
				.get("/api/card/receive")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.jsonPath()
				.getList("data.cards", CardResponse.class);
			assertThat(cards).isEmpty();
		}

		@Test
		void 존재하지_않는_받은_명함_삭제시_404_예외를_반환한다() {
			// given
			RemoveReceivedCardsRequest request = new RemoveReceivedCardsRequest(
				List.of(9999L)
			);

			// when, then
			String errorMessage = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.delete("/api/card/receive")
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract()
				.jsonPath()
				.getString("message");
			assertThat(errorMessage).isEqualTo(CardErrorCode.RECEIVED_CARD_NOT_FOUND.getMessage());
		}
	}

	@Nested
	class 받은_명함_업데이트 {

		@Test
		void 받은_명함_메모_업데이트_성공() {
			// given
			User cardOwner = userFixture.creator()
				.name("명함소유자")
				.create();
			Card card = cardFixture.creator()
				.user(cardOwner)
				.create();
			receivedCardFixture.creator()
				.user(mockUser)
				.card(card)
				.create();

			// given - Update request
			FixReceivedCardRequest request = new FixReceivedCardRequest(
				card.getId(),
				"중요한 비즈니스 파트너"
			);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.put("/api/card/receive")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("status")).isEqualTo("OK");
			assertThat(responseMap.get("message")).isEqualTo("명함 업데이트 성공");
		}

		@Test
		void 존재하지_않는_받은_명함_업데이트시_404_예외를_반환한다() {
			// given
			FixReceivedCardRequest request = new FixReceivedCardRequest(
				9999L,
				"존재하지 않는 명함 메모"
			);

			// when
			ExtractableResponse<Response> response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("Authorization", authToken)
				.body(request)
				.when()
				.put("/api/card/receive")
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract();

			// then
			Map<String, Object> responseMap = response.as(Map.class);
			assertThat(responseMap.get("message")).isEqualTo(CardErrorCode.RECEIVED_CARD_NOT_FOUND.getMessage());
		}
	}

	@Nested
	class 명함_수정 {

		private Career career1;
		private Career career2;
		private Card existCard;
		private MockMultipartFile testImageFile;
		private final String existingImageKey = "card-profiles/existing-image.jpg"; // Example existing key

		@BeforeEach
		void setUp() {
			career1 = careerFixture.serverDeveloper();
			career2 = careerFixture.productDesigner();

			existCard = cardFixture.creator()
				.user(mockUser)
				.nickname("닉네임1")
				.previewInfo(PreviewInfoType.PROJECT)
				.career(career1)
				.imagePath(existingImageKey)
				.create();

			testImageFile = new MockMultipartFile(
				"profileImage",
				"new-image.jpg",
				MediaType.IMAGE_JPEG_VALUE,
				"new image content".getBytes()
			);

		}

		@Test
		void 명함_수정_성공_모든_필드_업데이트_및_이미지_교체() throws IOException {
			// given
			String newNickname = "newNickname";
			String newSummary = "newSummary";
			Long newDetailJobId = career2.getId();
			List<String> newInterestDomain = List.of("newInterestDomain");
			String newOrganization = "newOrganization";
			String newRegion = "newRegion";
			String newHobby = "newHobby";
			String newNews = "newNews";
			PreviewInfoType newPreviewType = PreviewInfoType.HOBBY;
			List<Map<String, String>> newSns = List.of(Map.of("type", "GITHUB", "link", "https://github.com/new"));
			List<Map<String, String>> newContent = List.of(
				Map.of("title", "newContent", "link", "https://blog.com/new"));
			List<Map<String, String>> newProject = List.of(
				Map.of("title", "newProject", "link", "https://project.com/new"));

			// when
			ExtractableResponse<Response> response = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("cardId", existCard.getId())
				.multiPart("nickname", newNickname)
				.multiPart("detailJobId", newDetailJobId)
				.multiPart("interestDomain", objectMapper.writeValueAsString(newInterestDomain))
				.multiPart("summary", newSummary)
				.multiPart("organization", newOrganization)
				.multiPart("region", newRegion)
				.multiPart("hobby", newHobby)
				.multiPart("news", newNews)
				.multiPart("previewInfoType", newPreviewType.name())
				.multiPart("sns", objectMapper.writeValueAsString(newSns))
				.multiPart("content", objectMapper.writeValueAsString(newContent))
				.multiPart("project", objectMapper.writeValueAsString(newProject))
				.multiPart("profileImage", testImageFile.getOriginalFilename(), testImageFile.getBytes(),
					testImageFile.getContentType())
				.when()
				.put("/api/card")
				.then().log().all()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			assertThat(response.jsonPath().getString("status")).isEqualTo("OK");
			assertThat(response.jsonPath().getString("message")).isEqualTo("내 명함 수정 성공");

			ExtractableResponse<Response> detailResponse = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.queryParam("cardId", existCard.getId())
				.when()
				.get("/api/card/detail")
				.then().log().all()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// 상세 조회 결과 검증
			Map<String, Object> dataMap = detailResponse.jsonPath().getMap("data");
			assertThat(dataMap.get("nickname")).isEqualTo(newNickname);
			assertThat(dataMap.get("summary")).isEqualTo(newSummary);
			assertThat(dataMap.get("job")).isEqualTo(career2.getJob().name());
			assertThat(dataMap.get("detailJob")).isEqualTo(career2.getDetailJobEn());
			assertThat(dataMap.get("organization")).isEqualTo(newOrganization);
			assertThat((List<String>)dataMap.get("interestDomain")).containsExactlyElementsOf(newInterestDomain);
			assertThat(dataMap).containsKey("imagePath");
			List<Map<String, Object>> snsList = (List<Map<String, Object>>)dataMap.get("sns");
			assertThat(snsList).hasSize(1);
			assertThat(snsList.get(0).get("type")).isEqualTo("GITHUB");
			assertThat(snsList.get(0).get("link")).isEqualTo("https://github.com/new");
			List<Map<String, Object>> contentList = (List<Map<String, Object>>)dataMap.get("content");
			assertThat(contentList).hasSize(1);
			assertThat(contentList.get(0).get("title")).isEqualTo("newContent");
			assertThat(contentList.get(0).get("link")).isEqualTo("https://blog.com/new");
			List<Map<String, Object>> projectList = (List<Map<String, Object>>)dataMap.get("project");
			assertThat(projectList).hasSize(1);
			assertThat(projectList.get(0).get("title")).isEqualTo("newProject");
			assertThat(projectList.get(0).get("link")).isEqualTo("https://project.com/new");

		}

		@Test
		void 명함_수정_성공_기존_이미지_유지() throws JsonProcessingException {
			// given
			String updatedNickname = "newNickname";

			// when
			ExtractableResponse<Response> response = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("cardId", existCard.getId())
				.multiPart("nickname", updatedNickname)
				.multiPart("detailJobId", existCard.getCareer().getId())
				.multiPart("interestDomain", objectMapper.writeValueAsString(existCard.getInterestDomain()))
				.multiPart("summary", "newSummary")
				.multiPart("isImageRemoved", false)
				.multiPart("previewInfoType", PreviewInfoType.SNS)
				.when()
				.put("/api/card")
				.then().log().all()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			ExtractableResponse<Response> detailResponse = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.queryParam("cardId", existCard.getId())
				.when()
				.get("/api/card/detail")
				.then().log().all()
				.statusCode(HttpStatus.OK.value())
				.extract();

			Map<String, Object> dataMap = detailResponse.jsonPath().getMap("data");
			assertThat(dataMap.get("nickname")).isEqualTo(updatedNickname);
			assertThat(dataMap.get("imagePath")).isNotNull();
			assertThat(dataMap.get("summary")).isEqualTo("newSummary");
		}

		@Test
		void 명함_수정_성공_선택_필드_null_업데이트() throws JsonProcessingException {
			// when
			ExtractableResponse<Response> response = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("cardId", existCard.getId())
				.multiPart("nickname", "newNick")
				.multiPart("detailJobId", career1.getId())
				.multiPart("interestDomain", objectMapper.writeValueAsString(existCard.getInterestDomain()))
				.multiPart("summary", "summary")
				.multiPart("isImageRemoved", false)
				.multiPart("previewInfoType", PreviewInfoType.SNS)
				.when()
				.put("/api/card")
				.then().log().all()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			ExtractableResponse<Response> detailResponse = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.queryParam("cardId", existCard.getId())
				.when()
				.get("/api/card/detail")
				.then().log().all()
				.statusCode(HttpStatus.OK.value())
				.extract();

			Map<String, Object> dataMap = detailResponse.jsonPath().getMap("data");

			assertThat(dataMap.get("nickname")).isEqualTo("newNick");
			assertThat(dataMap.get("organization")).isNull();
			assertThat(dataMap.get("hobby")).isNull();
			assertThat(dataMap.get("news")).isNull();
			assertThat(dataMap.get("previewInfo")).isNull();
			assertThat(dataMap.get("sns")).isNull();
			assertThat(dataMap.get("content")).isNull();
			assertThat(dataMap.get("project")).isNull();

			// assertThat(updatedCard.getImagePath()).isEqualTo(existingImageKey); // Image kept
		}

		@Test
		void 명함_수정_성공_이미지_삭제() throws JsonProcessingException {
			// given: Update fields, but send NO profileImage and NO originImageKey

			// when
			ExtractableResponse<Response> response = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("cardId", existCard.getId())
				.multiPart("nickname", "noImage")
				.multiPart("detailJobId", career1.getId())
				.multiPart("interestDomain", objectMapper.writeValueAsString(existCard.getInterestDomain()))
				.multiPart("summary", existCard.getSummary())
				.multiPart("isImageRemoved", true)
				.multiPart("previewInfoType", PreviewInfoType.SNS)
				.when()
				.put("/api/card")
				.then().log().all()
				.statusCode(HttpStatus.OK.value())
				.extract();

			// then
			ExtractableResponse<Response> detailResponse = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.queryParam("cardId", existCard.getId())
				.when()
				.get("/api/card/detail")
				.then().log().all()
				.statusCode(HttpStatus.OK.value())
				.extract();

			Map<String, Object> dataMap = detailResponse.jsonPath().getMap("data");

			assertThat(dataMap.get("nickname")).isEqualTo("noImage");
			assertThat((String)dataMap.get("imagePath")).contains("base-image");

		}

		@Test
		void 명함_수정_실패_필수_필드_누락_닉네임() throws JsonProcessingException {
			// when
			given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("cardId", existCard.getId())
				.multiPart("detailJobId", career1.getId())
				.multiPart("interestDomain", objectMapper.writeValueAsString(existCard.getInterestDomain()))
				.multiPart("summary", "요약")
				.multiPart("originImageKey", existingImageKey)
				.multiPart("previewInfoType", PreviewInfoType.SNS)
				.when()
				.put("/api/card")
				.then().log().all()
				.statusCode(HttpStatus.BAD_REQUEST.value());
		}

		@Test
		void 명함_수정_실패_존재하지_않는_명함() throws JsonProcessingException {
			// given
			Long nonExistentCardId = 9999L;

			// when
			ExtractableResponse<Response> response = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("cardId", nonExistentCardId)
				.multiPart("nickname", "수정 시도")
				.multiPart("detailJobId", career1.getId())
				.multiPart("interestDomain", objectMapper.writeValueAsString(existCard.getInterestDomain()))
				.multiPart("summary", "요약")
				.multiPart("previewInfoType", PreviewInfoType.SNS)
				.when()
				.put("/api/card")
				.then().log().all()
				.statusCode(HttpStatus.NOT_FOUND.value())
				.extract();

			// then
			assertThat(response.jsonPath().getString("message"))
				.isEqualTo(CardErrorCode.CARD_NOT_FOUND.getMessage());
		}

		@Test
		void 명함_수정_실패_권한_없음_다른_사용자_명함() throws JsonProcessingException {
			// given
			User otherUser = userFixture.creator().name("다른사용자").create();
			Card otherUsersCard = cardFixture.creator()
				.user(otherUser)
				.career(career2)
				.nickname("nick")
				.summary("sum")
				.create();

			// when
			ExtractableResponse<Response> response = given().log().all()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("cardId", otherUsersCard.getId())
				.multiPart("nickname", "newNickname")
				.multiPart("detailJobId", career2.getId())
				.multiPart("interestDomain", objectMapper.writeValueAsString(existCard.getInterestDomain()))
				.multiPart("summary", "summary")
				.multiPart("isImageRemoved", false)
				.multiPart("previewInfoType", PreviewInfoType.SNS)
				.when()
				.put("/api/card")
				.then().log().all()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract();

			// then
			assertThat(response.jsonPath().getString("message"))
				.isEqualTo(CardErrorCode.INVALID_CARD_OWNER.getMessage());
		}

		@Test
		void 명함_수정_실패_인증_없음() throws JsonProcessingException {
			// when
			given().log().all()
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("cardId", existCard.getId())
				.multiPart("nickname", "newNickname")
				.multiPart("detailJobId", career1.getId())
				.multiPart("interestDomain", objectMapper.writeValueAsString(existCard.getInterestDomain()))
				.multiPart("summary", "summary")
				.multiPart("originImageKey", existingImageKey)
				.when()
				.put("/api/card")
				.then().log().all()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
		}
	}

	@Nested
	class 대표_명함_시나리오 {

		@Test
		void 최초_명함은_자동으로_대표로_지정된다() throws IOException {
			// given
			MockMultipartFile profileImage = new MockMultipartFile(
				"profileImage",
				"first.jpg",
				"image/jpeg",
				"fake-image".getBytes()
			);

			// when
			given()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType("multipart/form-data")
				.multiPart("profileImage", profileImage.getOriginalFilename(), profileImage.getBytes(),
					profileImage.getContentType())
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
				.then().statusCode(201);

			// then
			List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(mockUser.getId());
			assertThat(cards).hasSize(1);
			assertThat(cards.get(0).isPrimary()).isTrue();
		}

		@Test
		void 기존_명함이_있는_상태에서_생성된_명함은_대표가_아니다() throws IOException {
			// given
			cardFixture.creator().user(mockUser).isPrimary(true).create();

			MockMultipartFile profileImage = new MockMultipartFile(
				"profileImage",
				"second.jpg",
				"image/jpeg",
				"fake-image".getBytes()
			);

			// when
			given()
				.header("Authorization", "Bearer %s".formatted(authToken))
				.contentType("multipart/form-data")
				.multiPart("previewInfoType", "SNS")
				.multiPart("profileImage", profileImage.getOriginalFilename(), profileImage.getBytes(),
					profileImage.getContentType())
				.multiPart("nickname", "윤장원2")
				.multiPart("detailJobId", "1")
				.multiPart("interestDomain", "[\"웹\"]")
				.multiPart("summary", "두 번째 명함")
				.when().post("/api/card")
				.then().statusCode(201);

			// then
			List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(mockUser.getId());
			assertThat(cards).hasSize(2);
			long primaryCount = cards.stream().filter(Card::isPrimary).count();
			assertThat(primaryCount).isEqualTo(1);
			assertThat(
				cards.stream().anyMatch(card -> !card.isPrimary())).isTrue();
		}

		@Test
		void 대표_명함을_수동으로_설정하면_기존_대표가_해제된다() {
			// given
			Card card1 = cardFixture.creator().user(mockUser).nickname("기존대표").isPrimary(true).create();
			Card card2 = cardFixture.creator().user(mockUser).nickname("신규후보").create();

			// when
			given()
				.header("Authorization", authToken)
				.contentType(ContentType.JSON)
				.when()
				.post("/api/card/{cardId}/primary", card2.getId())
				.then()
				.statusCode(HttpStatus.OK.value());

			// then
			Card updatedCard1 = cardRepository.findById(card1.getId()).orElseThrow();
			Card updatedCard2 = cardRepository.findById(card2.getId()).orElseThrow();
			assertThat(updatedCard1.isPrimary()).isFalse();
			assertThat(updatedCard2.isPrimary()).isTrue();
		}

		@Test
		void 대표_명함_삭제시_남은_한_명이_대표로_승격된다() {
			// given
			Card card1 = cardFixture.creator().user(mockUser).nickname("대표").isPrimary(true).create();
			Card card2 = cardFixture.creator().user(mockUser).nickname("후보").create();

			// when
			given()
				.header("Authorization", authToken)
				.contentType(ContentType.JSON)
				.when()
				.delete("/api/card/{cardId}", card1.getId())
				.then()
				.statusCode(HttpStatus.NO_CONTENT.value());

			// then
			Card updatedCard2 = cardRepository.findById(card2.getId()).orElseThrow();
			Card deletedCard1 = cardRepository.findById(card1.getId()).orElseThrow();
			assertThat(deletedCard1.getDeletedAt()).isNotNull();
			assertThat(updatedCard2.isPrimary()).isTrue();
		}

		@Test
		void 대표_명함_삭제시_여러_명함_중_임의_하나가_대표로_선정된다() {
			// given
			Card card1 = cardFixture.creator().user(mockUser).nickname("대표").isPrimary(true).create();
			Card card2 = cardFixture.creator().user(mockUser).nickname("후보1").create();
			Card card3 = cardFixture.creator().user(mockUser).nickname("후보2").create();

			// when
			given()
				.header("Authorization", authToken)
				.contentType(ContentType.JSON)
				.when()
				.delete("/api/card/{cardId}", card1.getId())
				.then()
				.statusCode(HttpStatus.NO_CONTENT.value());

			// then
			Card updatedCard2 = cardRepository.findById(card2.getId()).orElseThrow();
			Card updatedCard3 = cardRepository.findById(card3.getId()).orElseThrow();

			long primaryCount = 0;
			if (updatedCard2.isPrimary())
				primaryCount++;
			if (updatedCard3.isPrimary())
				primaryCount++;

			assertThat(primaryCount).isEqualTo(1);
		}

		@Test
		void 모든_명함을_삭제하면_대표_명함은_존재하지_않는다() {
			// given
			Card card1 = cardFixture.creator().user(mockUser).create();
			Card card2 = cardFixture.creator().user(mockUser).create();

			// when
			given().header("Authorization", authToken)
				.when().delete("/api/card/{cardId}", card1.getId())
				.then().statusCode(HttpStatus.NO_CONTENT.value());

			given().header("Authorization", authToken)
				.when().delete("/api/card/{cardId}", card2.getId())
				.then().statusCode(HttpStatus.NO_CONTENT.value());

			// then
			List<Card> remaining = cardRepository.findAllByUserIdAndDeletedAtIsNull(mockUser.getId());
			assertThat(remaining).isEmpty();
		}
	}

	@Nested
	class 명함_조회_우선순위 {

		@Test
		void 대표_명함이_항상_가장_먼저_조회된다_중간() {
			// given
			cardFixture.creator().user(mockUser).nickname("후보1").isPrimary(false).create();
			cardFixture.creator().user(mockUser).nickname("대표").isPrimary(true).create();
			cardFixture.creator().user(mockUser).nickname("후보2").isPrimary(false).create();

			// when
			MyCardListResponse response = given()
				.contentType(ContentType.JSON)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/my")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.jsonPath()
				.getObject("data", MyCardListResponse.class);

			// then
			assertThat(response.cards()).hasSize(3);
			assertThat(response.cards().get(0).nickname()).isEqualTo("대표");
			assertThat(response.cards().get(0).isPrimary()).isEqualTo(true);
			assertThat(response.cards().get(1).isPrimary()).isEqualTo(false);
			assertThat(response.cards().get(2).isPrimary()).isEqualTo(false);
		}

		@Test
		void 대표_명함이_항상_가장_먼저_조회된다_끝() {
			// given
			cardFixture.creator().user(mockUser).nickname("후보1").isPrimary(false).create();
			cardFixture.creator().user(mockUser).nickname("후보2").isPrimary(false).create();
			cardFixture.creator().user(mockUser).nickname("대표").isPrimary(true).create();

			// when
			MyCardListResponse response = given()
				.contentType(ContentType.JSON)
				.header("Authorization", authToken)
				.when()
				.get("/api/card/my")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.jsonPath()
				.getObject("data", MyCardListResponse.class);

			// then
			assertThat(response.cards()).hasSize(3);
			assertThat(response.cards().get(0).nickname()).isEqualTo("대표");
			assertThat(response.cards().get(0).isPrimary()).isEqualTo(true);
			assertThat(response.cards().get(1).isPrimary()).isEqualTo(false);
			assertThat(response.cards().get(2).isPrimary()).isEqualTo(false);
		}
	}
}
