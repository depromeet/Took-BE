package com.evenly.took.feature.auth.client.google;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.config.properties.GoogleProperties;
import com.evenly.took.feature.auth.config.properties.GoogleUrlProperties;
import com.evenly.took.global.service.MockTest;

public abstract class MockGoogleProviderTest extends MockTest {

	@Mock
	protected GoogleProperties googleProperties;

	@Mock
	protected GoogleUrlProperties googleUrlProperties;

	@Mock
	protected RestClient.Builder restClientBuilder;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	protected RestClient restClient;

	@BeforeEach
	public void setUpCommon() {
		when(restClientBuilder.clone()).thenReturn(restClientBuilder);
		when(restClientBuilder.defaultStatusHandler(any())).thenReturn(restClientBuilder);
		when(restClientBuilder.defaultHeader("Content-Type", "application/x-www-form-urlencoded", "UTF-8"))
			.thenReturn(restClientBuilder);
		when(restClientBuilder.build()).thenReturn(restClient);
	}
}
