package com.evenly.took.feature.auth.client;

import lombok.Getter;

@Getter
public class AuthContext {
	private final String authCode;
	private final String name;

	// 기본 생성자 - 인증 코드만 있는 경우
	public AuthContext(String authCode) {
		this(authCode, null);
	}

	// 이름 정보까지 있는 경우
	public AuthContext(String authCode, String name) {
		this.authCode = authCode;
		this.name = name;
	}

	public boolean hasName() {
		return name != null && !name.isEmpty();
	}
}
