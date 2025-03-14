package com.evenly.took.feature.card.api;

import static io.restassured.RestAssured.*;
import static org.hamcrest.core.IsEqual.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.aws.s3.S3Service;
import com.evenly.took.global.config.testcontainers.S3TestConfig;
import com.evenly.took.global.domain.TestUserFactory;
import com.evenly.took.global.integration.IntegrationTest;

@Import(S3TestConfig.class)
public class CardIntegrationTest extends IntegrationTest {

	@MockitoBean
	UserClientComposite userClientComposite; // 로그인 AccessToken 발급 목적

	@MockitoBean
	CardRepository cardRepository;

	@MockitoBean
	private S3Service s3Service;

	@Nested
	class 명함_생성 {

		private MockMultipartFile testImageFile;
		private final String TEST_PATH = "cards/";

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
			// Given
			User user = TestUserFactory.createMockUser("took");
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			// When & Then
			try {
				given().log().all()
					.header("Authorization", "Bearer %s".formatted(tokens.accessToken()))
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
		void 명함_개수가_최대치를_초과하면_예외를_반환한다() {
			// Given
			User user = TestUserFactory.createMockUser("took");
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			// Create a list of mock cards
			List<Card> mockCards = Arrays.asList(
				mock(Card.class),
				mock(Card.class),
				mock(Card.class),
				mock(Card.class)
			);

			// Mock the cardRepository to return the list of mock cards
			BDDMockito.given(cardRepository.countByUserIdAndDeletedAtIsNull(anyLong()))
				.willReturn((long)mockCards.size());

			// When & Then
			try {
				given().log().all()
					.header("Authorization", "Bearer %s".formatted(tokens.accessToken()))
					.contentType("multipart/form-data")
					.multiPart("profileImage", testImageFile.getOriginalFilename(),
						testImageFile.getInputStream(), testImageFile.getContentType())
					.multiPart("nickname", "윤장원")
					.multiPart("detailJobId", "1")
					.multiPart("interestDomain", "[\"웹\", \"모바일\", \"클라우드\"]")
					.multiPart("summary", "백엔드 개발을 좋아하는 개발자입니다")
					.when().post("/api/card")
					.then().log().all()
					.statusCode(400)
					.body("message", equalTo(CardErrorCode.CARD_LIMIT_EXCEEDED.getMessage()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Test
		void 필수_필드가_누락된_경우_예외를_반환한다() {
			// Given
			User user = TestUserFactory.createMockUser("took");
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			try {
				given().log().all()
					.header("Authorization", "Bearer %s".formatted(tokens.accessToken()))
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
			// Given
			User user = TestUserFactory.createMockUser("took");
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			// When & Then
			given().log().all()
				.header("Authorization", "Bearer %s".formatted(tokens.accessToken()))
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
}
