package com.evenly.took.feature.auth.application;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;
import com.evenly.took.global.config.properties.auth.TokenProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final TokenProperties tokenProperties;

	public String generateAccessToken(String userId) {
		Claims claims = generateClaims(userId);
		Date now = new Date();
		Date expiredAt = new Date(now.getTime() + tokenProperties.accessTokenExpirationMilliTime());
		return buildAccessToken(claims, now, expiredAt);
	}

	private Claims generateClaims(String userId) {
		return Jwts.claims()
			.setSubject(userId);
	}

	private String buildAccessToken(Claims claims, Date now, Date expiredAt) {
		Key key = getSigningKey();
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(expiredAt)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public void validateToken(String token) {
		try {
			parseClaims(token);
		} catch (JwtException | IllegalArgumentException e) {
			log.error(AuthErrorCode.INVALID_ACCESS_TOKEN.getMessage(), e);
			throw new TookException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
	}

	public String getUserId(String token) {
		try {
			return parseClaims(token)
				.getBody()
				.getSubject();
		} catch (JwtException ex) {
			throw new TookException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
	}

	private Jws<Claims> parseClaims(String token) {
		Key key = getSigningKey();
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
	}

	private Key getSigningKey() {
		String secret = tokenProperties.accessTokenSecret();
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
