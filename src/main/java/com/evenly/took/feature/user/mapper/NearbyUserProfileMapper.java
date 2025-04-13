package com.evenly.took.feature.user.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.dto.response.NearbyUserBasicProfileResponse;
import com.evenly.took.feature.user.dto.response.NearbyUserCardProfileResponse;
import com.evenly.took.feature.user.dto.response.NearbyUserProfileResponse;
import com.evenly.took.global.aws.s3.S3Service;
import com.evenly.took.global.redis.dto.NearbyUserDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NearbyUserProfileMapper {

	private final S3Service s3Service;

	public List<NearbyUserProfileResponse> toProfiles(
		List<NearbyUserDto> nearbyUsers,
		Long myUserId,
		Map<Long, Card> cardMap,
		Map<Long, User> userMap
	) {
		return nearbyUsers.stream()
			.map(dto -> {
				Long userId = Long.parseLong(dto.userId());
				if (Objects.equals(userId, myUserId))
					return null;

				Card card = cardMap.get(userId);
				if (card != null) {
					return new NearbyUserCardProfileResponse(
						userId,
						card.getId(),
						card.getNickname(),
						card.getCareer().getDetailJobEn(),
						s3Service.generatePresignedViewUrl(card.getImagePath())
					);
				}

				User user = userMap.get(userId);
				if (user != null) {
					return new NearbyUserBasicProfileResponse(user.getId(), user.getName());
				}

				return null;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}
}
