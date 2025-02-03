package com.evenly.blok.global.config.swagger;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.evenly.blok.global.common.constants.UrlConstants;
import com.evenly.blok.global.util.ProfileResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

	// TODO: 서비스명 정해지면 바꾸기
	private static final String SERVER_NAME = "16th 2ven";
	private static final String API_TITLE = "16th 2ven 서버 API 문서";
	private static final String API_DESCRIPTION = "16th 2ven 서버 API 문서입니다.";
	private static final String GITHUB_URL = "https://github.com/depromeet/16th-team2-BE";

	private final ProfileResolver profileResolver;

	@Value("${swagger.version}")
	private String version;

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.servers(swaggerServers())
			.components(swaggerComponents())
			.info(swaggerInfo());
	}

	private List<Server> swaggerServers() {
		Server server = new Server().url(getServerUrl()).description(API_DESCRIPTION);
		return List.of(server);
	}

	private Components swaggerComponents() {
		return new Components()
			.addSecuritySchemes("bearerAuth", new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.in(SecurityScheme.In.HEADER)
				.name("Authorization"));
	}

	private Info swaggerInfo() {
		License license = new License();
		license.setUrl(GITHUB_URL);
		license.setName(SERVER_NAME);

		return new Info()
			.version("v" + version)
			.title(API_TITLE)
			.description(API_DESCRIPTION)
			.license(license);
	}

	@Bean
	public ModelResolver modelResolver(ObjectMapper objectMapper) {
		return new ModelResolver(objectMapper);
	}

	private String getServerUrl() {
		return switch (profileResolver.getCurrentProfile()) {
			// TODO: prod, dev 연결
			// case "prod" -> UrlConstants.PROD_SERVER_URL.getValue();
			// case "dev" -> UrlConstants.DEV_SERVER_URL.getValue();
			default -> UrlConstants.LOCAL_SERVER_URL.getValue();
		};
	}
}
