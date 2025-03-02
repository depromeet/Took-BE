package com.evenly.took.global.service;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.config.AuthConfig;
import com.evenly.took.global.config.client.ClientConfig;

@ActiveProfiles("test")
@Import({ClientConfig.class, AuthConfig.class})
public abstract class BaseRestClientTest {

	@Autowired
	protected RestClient.Builder restClientBuilder;

	protected MockRestServiceServer mockServer;

	protected void configure200MockServer(String requestUri, String responseBody) {
		mockServer.expect(requestTo(requestUri))
			.andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(method(HttpMethod.POST))
			.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
	}

	protected void configure400MockServer(String requestUri, String responseBody) {
		mockServer.expect(requestTo(requestUri))
			.andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(method(HttpMethod.POST))
			.andRespond(withBadRequest().body(responseBody).contentType(MediaType.APPLICATION_JSON));
	}

	protected void configure401MockServer(String requestUri) {
		mockServer.expect(requestTo(requestUri))
			.andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(method(HttpMethod.POST))
			.andRespond(withUnauthorizedRequest());
	}

	protected void configure401MockServer(String requestUri, String responseBody) {
		mockServer.expect(requestTo(requestUri))
			.andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(method(HttpMethod.POST))
			.andRespond(withUnauthorizedRequest().body(responseBody).contentType(MediaType.APPLICATION_JSON));
	}

	protected String readResourceFile(String fileName) throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		String resourcePath = Objects.requireNonNull(classLoader.getResource(fileName)).getPath();
		Path path = Path.of(resourcePath);
		return Files.readString(path);
	}
}
