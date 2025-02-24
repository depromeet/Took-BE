package com.evenly.took.global.redis;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.config.testcontainers.RedisTestConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class RedisTest {

	@Autowired
	private RedisService redisService;

	@Test
	@DisplayName("Redis 값을 저장 및 조회")
	void setValue_getValue() {
		// given
		String key = "test_key";
		String value = "test_value";

		// when
		boolean isSuccess = redisService.setValue(key, value);

		// then
		assertThat(isSuccess).isTrue();
		assertThat(redisService.getValue(key)).isEqualTo(value);
	}

	@Test
	@DisplayName("TTL 설정 후, 만료되는지 확인")
	void setValueWithTTL() throws InterruptedException {
		// given
		String key = "test_ttl_key";
		String value = "test_ttl_value";
		Duration ttl = Duration.ofSeconds(1);

		// when
		boolean isSuccess = redisService.setValueWithTTL(key, value, ttl);

		// then
		assertThat(isSuccess).isTrue();
		assertThat(redisService.getValue(key)).isEqualTo(value);

		// TTL 만료 대기
		Thread.sleep(1100);
		assertThat(redisService.getValue(key)).isNull();
	}

	@Test
	@DisplayName("저장된 키를 삭제")
	void deleteKey() {
		// given
		String key = "test_delete_key";
		String value = "test_delete_value";
		redisService.setValue(key, value);

		// when
		boolean isSuccess = redisService.deleteKey(key);

		// then
		assertThat(isSuccess).isTrue();
		assertThat(redisService.getValue(key)).isNull();
	}
}