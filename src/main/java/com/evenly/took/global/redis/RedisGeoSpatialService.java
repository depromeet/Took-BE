package com.evenly.took.global.redis;

import java.time.Duration;
import java.util.List;

import org.springframework.data.geo.Point;

import com.evenly.took.global.redis.dto.NearbyUserDto;

public interface RedisGeoSpatialService {

	/**
	 * 사용자 위치 등록
	 *
	 * @param userId    사용자 ID
	 * @param longitude 경도
	 * @param latitude  위도
	 * @param sessionId 세션 ID
	 * @return 등록 성공 여부
	 */
	boolean registerUserLocation(String userId, double longitude, double latitude, String sessionId);

	/**
	 * 주변 사용자 검색
	 *
	 * @param longitude    현재 경도
	 * @param latitude     현재 위도
	 * @param radiusMeters 검색 반경 (미터)
	 * @param sessionId    세션 ID
	 * @return 주변 사용자 목록
	 */
	List<NearbyUserDto> findNearbyUsers(double longitude, double latitude,
		double radiusMeters, String sessionId);

	/**
	 * 두 사용자 간 거리 계산
	 *
	 * @param userId1   사용자1 ID
	 * @param userId2   사용자2 ID
	 * @param sessionId 세션 ID
	 * @return 거리 (미터), 실패 시 -1
	 */
	double calculateDistance(String userId1, String userId2, String sessionId);

	/**
	 * 사용자 위치 삭제
	 *
	 * @param userId    사용자 ID
	 * @param sessionId 세션 ID
	 * @return 삭제 성공 여부
	 */
	boolean removeUserLocation(String userId, String sessionId);

	/**
	 * 세션 데이터 만료 시간 설정
	 *
	 * @param sessionId 세션 ID
	 * @param ttl       만료 시간
	 * @return 설정 성공 여부
	 */
	boolean expireSessionData(String sessionId, Duration ttl);

	/**
	 * 사용자 위치 조회
	 *
	 * @param userId    사용자 ID
	 * @param sessionId 세션 ID
	 * @return 위치 좌표 (Point), 실패 시 null
	 */
	Point getUserPosition(String userId, String sessionId);

	/**
	 * 위치정보를 Geohash 문자열로 변환
	 *
	 * @param userId    사용자 ID
	 * @param sessionId 세션 ID
	 * @return Geohash 문자열, 실패 시 null
	 */
	String getUserGeohash(String userId, String sessionId);

	/**
	 * 세션 내 저장된 모든 사용자 ID 목록 조회
	 *
	 * @param sessionId 세션 ID
	 * @return 사용자 ID 목록
	 */
	List<String> getAllUsersInSession(String sessionId);
}
