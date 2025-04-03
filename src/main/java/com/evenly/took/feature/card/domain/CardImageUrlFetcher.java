package com.evenly.took.feature.card.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CardImageUrlFetcher {

	private static final String EMPTY_STRING = "";
	private static String prefix;

	public static String getImageUrl(String filePath) {
		return isValidPath(filePath) ? prefix + filePath : EMPTY_STRING;
	}

	private static boolean isValidPath(String filePath) {
		return filePath != null && !filePath.isBlank();
	}

	@Value("${aws.s3.url-prefix}")
	private void configureUrlPrefix(String urlPrefix) {
		prefix = urlPrefix;
	}
}
