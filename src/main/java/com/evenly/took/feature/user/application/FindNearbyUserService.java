package com.evenly.took.feature.user.application;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.dto.response.NearbyUserProfileResponse;
import com.evenly.took.feature.user.mapper.NearbyUserProfileMapper;
import com.evenly.took.global.location.util.LocationHeaderParser;
import com.evenly.took.global.redis.RedisGeoSpatialService;
import com.evenly.took.global.redis.dto.NearbyUserDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindNearbyUserService {

	private final RedisGeoSpatialService redisGeoSpatialService;
	private final UserRepository userRepository;
	private final CardRepository cardRepository;
	private final NearbyUserProfileMapper profileMapper;

	private static final double DEFAULT_RADIUS_METERS = 1000.0;

	@Transactional(readOnly = true)
	public List<NearbyUserProfileResponse> invoke(Long myUserId, HttpServletRequest request) {
		Point point = LocationHeaderParser.extractPoint(request);
		if (point == null) {
			return List.of();
		}

		List<NearbyUserDto> nearbyUserDtos = redisGeoSpatialService.findNearbyUsers(
			point.getX(), point.getY(), DEFAULT_RADIUS_METERS);

		List<Long> userIds = extractUserIds(nearbyUserDtos, myUserId);
		if (userIds.isEmpty()) {
			return List.of();
		}

		Map<Long, Card> primaryCardMap = getPrimaryCardMap(userIds);
		Map<Long, User> userMap = getUserMap(userIds);

		return profileMapper.toProfiles(nearbyUserDtos, myUserId, primaryCardMap, userMap);
	}

	private List<Long> extractUserIds(List<NearbyUserDto> dtos, Long myUserId) {
		return dtos.stream()
			.map(dto -> Long.parseLong(dto.userId()))
			.filter(id -> !Objects.equals(id, myUserId))
			.distinct()
			.collect(Collectors.toList());
	}

	private Map<Long, Card> getPrimaryCardMap(List<Long> userIds) {
		return cardRepository.findAllByUserIdInAndIsPrimaryTrueAndDeletedAtIsNull(userIds)
			.stream()
			.collect(Collectors.toMap(card -> card.getUser().getId(), Function.identity()));
	}

	private Map<Long, User> getUserMap(List<Long> userIds) {
		return userRepository.findAllById(userIds)
			.stream()
			.collect(Collectors.toMap(User::getId, Function.identity()));
	}
}
