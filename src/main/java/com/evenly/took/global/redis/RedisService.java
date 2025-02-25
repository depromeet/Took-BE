package com.evenly.took.global.redis;

import java.time.Duration;

public interface RedisService {

	/**
	 * Redis에 key-value를 저장합니다.
	 *
	 * @param key 저장할 키
	 * @param value 저장할 값
	 * @return 저장 성공 여부
	 */
	boolean setValue(String key, Object value);

	/**
	 * Redis에 key-value를 저장하고 만료 시간을 설정합니다.
	 *
	 * @param key 저장할 키
	 * @param value 저장할 값
	 * @param ttl 만료 시간
	 * @return 저장 성공 여부
	 */
	boolean setValueWithTTL(String key, Object value, Duration ttl);

	/**
	 * Redis에서 key에 해당하는 값을 조회합니다.
	 *
	 * @param key 조회할 키
	 * @return 저장된 값, 없거나 에러 발생 시 null
	 */
	Object getValue(String key);

	/**
	 * Redis에서 key를 삭제합니다.
	 *
	 * @param key 삭제할 키
	 * @return 삭제 성공 여부
	 */
	boolean deleteKey(String key);
}
