package com.evenly.took.global.logging;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.evenly.took.feature.user.dao.BlacklistedIPRepository;
import com.evenly.took.global.integration.IntegrationTest;

@Disabled // 필요시에만 테스트(다른 테스트에 영향)
class BlacklistedIPLoggingTest extends IntegrationTest {

	@Autowired
	private BlacklistedIPRepository blacklistedIPRepository;

	private static final String[] LOCAL_IPS = {"127.0.0.1", "0:0:0:0:0:0:0:1", "localhost"};

	@AfterEach
	void tearDown() {
		for (String ip : LOCAL_IPS) {
			clearIpFromBlacklist(ip);
		}
	}

	@Test
	void 요청_한도_초과_시_IP가_블랙리스트에_추가되는지_확인() throws Exception {
		// given
		String endpoint = "/api/health";
		int requestsToSend = 15;

		for (String ip : LOCAL_IPS) {
			clearIpFromBlacklist(ip);
		}

		boolean isInitiallyInBlacklist = isAnyLocalIpInBlacklist();
		assertTrue(!isInitiallyInBlacklist, "테스트 시작 전 IP가 블랙리스트에 없어야 합니다");

		// when
		ExecutorService executorService = Executors.newFixedThreadPool(5);

		for (int i = 0; i < requestsToSend; i++) {
			executorService.submit(() -> {
				try {
					given()
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.when()
						.get(endpoint);
				} catch (Exception e) {
					// 차단된 후의 요청은 예외가 발생할 수 있음 - 무시
				}
			});
		}

		executorService.shutdown();
		boolean completed = executorService.awaitTermination(30, TimeUnit.SECONDS);
		assertTrue(completed, "모든 요청이 30초 내에 처리되어야 합니다");

		Thread.sleep(3000);

		// then
		Path logPath = Paths.get("logs/blocking.log");
		List<String> logLines = Files.readAllLines(logPath);

		boolean isIpBlacklisted = logLines.stream()
			.anyMatch(line -> line.contains("IP 차단 - IP:") && line.contains("사유: 속도 제한 초과로 영구 블랙리스트에 추가"));

		boolean isInDatabase = isAnyLocalIpInBlacklist();

		// 첫 번째 검증 - 로그에 차단 메시지가 있어야 함
		assertTrue(isIpBlacklisted, "IP 주소가 블랙리스트에 추가되었다는 로그가 있어야 합니다");

		// 두 번째 검증 - 실제 DB에 추가되어 있어야 함
		assertTrue(isInDatabase, "IP 주소가 블랙리스트 데이터베이스에 추가되어야 합니다");

		// 세 번째 검증 - 차단된 후 요청 시 403 응답이 와야 함
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(endpoint)
			.then()
			.statusCode(HttpStatus.FORBIDDEN.value());
	}

	private void clearIpFromBlacklist(String ip) {
		try {
			blacklistedIPRepository.findByIpAddress(ip)
				.ifPresent(blacklistedIP -> blacklistedIPRepository.delete(blacklistedIP));
		} catch (Exception e) {
			System.err.println("블랙리스트에서 IP " + ip + " 삭제 중 오류 발생: " + e.getMessage());
		}
	}

	private boolean isIpInBlacklist(String ip) {
		try {
			return blacklistedIPRepository.findByIpAddress(ip).isPresent();
		} catch (Exception e) {
			System.err.println("블랙리스트 확인 중 오류 발생: " + e.getMessage());
			return false;
		}
	}

	private boolean isAnyLocalIpInBlacklist() {
		for (String ip : LOCAL_IPS) {
			if (isIpInBlacklist(ip)) {
				return true;
			}
		}
		return false;
	}
}
