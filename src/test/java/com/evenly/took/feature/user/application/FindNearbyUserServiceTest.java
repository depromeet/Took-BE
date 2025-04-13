package com.evenly.took.feature.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.dto.response.NearbyUserBasicProfileResponse;
import com.evenly.took.feature.user.dto.response.NearbyUserCardProfileResponse;
import com.evenly.took.feature.user.dto.response.NearbyUserProfileResponse;
import com.evenly.took.global.redis.RedisGeoSpatialService;
import com.evenly.took.global.service.ServiceTest;

import jakarta.servlet.http.HttpServletRequest;

class FindNearbyUserServiceTest extends ServiceTest {

	@Autowired
	private FindNearbyUserService findNearbyUserService;

	@Autowired
	private RedisGeoSpatialService redisGeoSpatialService;

	@MockitoBean
	private HttpServletRequest request;

	@Test
	void 대표_명함이_있는_경우_CardProfile_반환() {
		// given
		User user1 = userFixture.creator()
			.id(1L)
			.oauthIdentifier(OAuthIdentifier.builder().oauthId("o7").oauthType(OAuthType.APPLE).build())
			.create();

		User user2 = userFixture.creator()
			.id(2L)
			.oauthIdentifier(OAuthIdentifier.builder().oauthId("o1").oauthType(OAuthType.GOOGLE).build())
			.create();

		redisGeoSpatialService.registerUserLocation(user2.getId().toString(), 127.0, 37.5);

		Card card = cardFixture.creator()
			.user(user2)
			.nickname("닉네임")
			.career(careerFixture.frontendDeveloper())
			.isPrimary(true)
			.create();

		when(request.getHeader("x-redis-geo")).thenReturn("37.5, 127.0");

		// when
		List<NearbyUserProfileResponse> result = findNearbyUserService.invoke(user1.getId(), request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isInstanceOf(NearbyUserCardProfileResponse.class);
	}

	@Test
	void 대표_명함이_없는_경우_BasicProfile_반환() {
		// given
		User user1 = userFixture.creator()
			.id(1L)
			.oauthIdentifier(OAuthIdentifier.builder().oauthId("o7").oauthType(OAuthType.APPLE).build())
			.create();

		User user2 = userFixture.creator()
			.id(2L)
			.oauthIdentifier(OAuthIdentifier.builder().oauthId("o1").oauthType(OAuthType.GOOGLE).build())
			.create();

		redisGeoSpatialService.registerUserLocation(user2.getId().toString(), 127.0, 37.5);
		when(request.getHeader("x-redis-geo")).thenReturn("37.5, 127.0");

		// when
		List<NearbyUserProfileResponse> result = findNearbyUserService.invoke(user1.getId(), request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isInstanceOf(NearbyUserBasicProfileResponse.class);
	}
}
