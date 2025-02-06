package com.evenly.blok.global.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlConstants {

	LOCAL_SERVER_URL("http://localhost:8080"),
	;

	private final String value;
}
