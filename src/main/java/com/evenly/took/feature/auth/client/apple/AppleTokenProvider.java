package com.evenly.took.feature.auth.client.apple;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.evenly.took.feature.auth.client.apple.dto.request.AppleTokenRequest;
import com.evenly.took.feature.auth.client.apple.dto.response.AppleTokenResponse;
import com.evenly.took.feature.auth.client.apple.error.AppleTokenProviderErrorHandler;
import com.evenly.took.feature.auth.config.properties.AppleProperties;
import com.evenly.took.feature.auth.config.properties.AppleUrlProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppleTokenProvider {

	private final RestClient restClient;
	private final AppleProperties appleProperties;
	private final AppleUrlProperties appleUrlProperties;

	public AppleTokenProvider(RestClient.Builder restClientBuilder,
		AppleTokenProviderErrorHandler errorHandler,
		AppleProperties appleProperties,
		AppleUrlProperties appleUrlProperties) {
		this.restClient = restClientBuilder.clone()
			.defaultStatusHandler(errorHandler)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
				StandardCharsets.UTF_8.name())
			.build();
		this.appleProperties = appleProperties;
		this.appleUrlProperties = appleUrlProperties;
	}

	public AppleTokenResponse fetchIdToken(String authCode) {
		String clientSecret = generateClientSecret();
		AppleTokenRequest request = AppleTokenRequest.of(appleProperties, authCode, clientSecret);

		return restClient.post()
			.uri(appleUrlProperties.tokenUrl())
			.body(request.toMultiValueMap())
			.retrieve()
			.body(AppleTokenResponse.class);
	}

	private String generateClientSecret() {
		try {
			long now = System.currentTimeMillis() / 1000;
			long expiration = now + 7888500; // 3개월

			return Jwts.builder()
				.setHeaderParam("alg", "ES256")
				.setHeaderParam("kid", appleProperties.keyId())
				.setIssuer(appleProperties.teamId())
				.setIssuedAt(new Date(now * 1000))
				.setExpiration(new Date(expiration * 1000))
				.setAudience("https://appleid.apple.com")
				.setSubject(appleProperties.clientId())
				.signWith(getPrivateKey(), SignatureAlgorithm.ES256)
				.compact();
		} catch (Exception e) {
			log.error("Error generating client secret: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to generate client secret", e);
		}
	}

	private PrivateKey getPrivateKey() throws Exception {
		Path path = Paths.get(appleProperties.privateKeyPath());
		byte[] keyBytes = Files.readAllBytes(path);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("EC");
		return keyFactory.generatePrivate(keySpec);
	}
}
