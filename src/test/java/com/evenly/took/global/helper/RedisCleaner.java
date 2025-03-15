package com.evenly.took.global.helper;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ActiveProfiles("test")
public class RedisCleaner {

	private final RedisTemplate<String, Object> redisTemplate;

	public RedisCleaner(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void execute() {
		redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
	}
}
