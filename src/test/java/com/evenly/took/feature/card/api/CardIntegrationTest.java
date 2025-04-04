package com.evenly.took.feature.card.api;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import com.evenly.took.feature.card.domain.Card;
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
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.feature.card.exception.FolderErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.aws.s3.S3Service;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.integration.JwtMockIntegrationTest;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class CardIntegrationTest extends JwtMockIntegrationTest {

	@MockitoBean
	LinkExtractor linkExtractor;

	@Autowired
	S3Service s3Service;

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
}
