package com.evenly.took.global.redis;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

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
	void Redis_값을_저장_및_조회() {
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
	void TTL_설정_후_만료되는지_확인() throws InterruptedException {
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
	void 저장된_키를_삭제() {
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
