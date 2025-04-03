package com.evenly.took.feature.card.domain;

import org.springframework.beans.factory.annotation.Value;

public class CardImageUrlFetcher {

	private static final String EMPTY_STRING = "";

	@Value("${aws.s3.url-prefix}")
	private static String prefix;

	public static String fetchFullUri(String filePath) {
		if (isEmptyPath(filePath)) {
			return EMPTY_STRING;
		}
		return prefix + filePath;
	}

	private static boolean isEmptyPath(String filePath) {
		return filePath == null || filePath.isBlank();
	}
}
