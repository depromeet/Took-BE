package com.evenly.took.global.cache;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	public boolean setValue(String key, Object value) {
		return executeOperation(() -> {
			ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
			valueOps.set(key, value);
		});
	}

	public boolean setValueWithTTL(String key, Object value, Duration ttl) {
		return executeOperation(() -> {
			ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
			valueOps.set(key, value, ttl);
		});
	}

	public Object getValue(String key) {
		try {
			ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
			return valueOps.get(key);
		} catch (Exception e) {
			log.error("Redis Get Value Error :: {}", e.getMessage(), e);
			return null;
		}
	}

	public boolean deleteKey(String key) {
		return executeOperation(() -> {
			redisTemplate.delete(key);
		});
	}

	private boolean executeOperation(Runnable operation) {
		try {
			operation.run();
			return true;
		} catch (Exception e) {
			log.error("Redis Operation Error :: {}", e.getMessage(), e);
			return false;
		}
	}
}
