package com.evenly.took.domain.auth.jwt;

import static com.evenly.took.global.exception.auth.jwt.JwtErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.evenly.took.domain.user.domain.User;
import com.evenly.took.global.config.properties.jwt.JwtProperties;

import io.jsonwebtoken.Claims;
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

	public String generateAccessToken(User user) {
		Claims claims = generateClaims(user);
		Date now = new Date();
		Date expiredAt = new Date(now.getTime() + jwtProperties.accessTokenExpirationMilliTime());
		return buildAccessToken(claims, now, expiredAt);
	}

	private Claims generateClaims(final User user) {
		Claims claims = Jwts.claims().setSubject(user.getId().toString());
		claims.put("name", user.getName());
		return claims;
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

	public boolean validateToken(String token) {
		Key key = getSigningKey();
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.error(JWT_UNAUTHORIZED.getMessage());
			return false;
		}
	}

	public String getUserId(String token) {
		Key key = getSigningKey();
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	private Key getSigningKey() {
		String accessTokenSecret = jwtProperties.accessTokenSecret();
		return Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
	}
}
