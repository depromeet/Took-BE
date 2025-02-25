package com.evenly.took.global.security.jwt;

import static com.evenly.took.global.exception.auth.jwt.AuthErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.evenly.took.global.config.properties.jwt.JwtProperties;
import com.evenly.took.global.exception.auth.oauth.InvalidAccessTokenException;

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

	private final JwtProperties jwtProperties;

	public String generateAccessToken(String userId) {
		Claims claims = generateClaims(userId);
		Date now = new Date();
		Date expiredAt = new Date(now.getTime() + jwtProperties.accessTokenExpirationMilliTime());
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
			log.error(JWT_UNAUTHORIZED.getMessage(), e);
			throw new InvalidAccessTokenException(JWT_UNAUTHORIZED);
		}
	}

	public String getUserId(String token) {
		return parseClaims(token)
			.getBody()
			.getSubject();
	}

	private Jws<Claims> parseClaims(String token) {
		Key key = getSigningKey();
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
	}

	private Key getSigningKey() {
		String secret = jwtProperties.accessTokenSecret();
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
