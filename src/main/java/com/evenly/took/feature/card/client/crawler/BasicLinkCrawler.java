package com.evenly.took.feature.card.client.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.LinkCrawler;
import com.evenly.took.feature.card.client.LinkSource;
import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.global.exception.TookException;

@Component
public class BasicLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";
	private static final String DELIMITER_OF_BASE_URL = "://";

	@Override
	public LinkSource supportSource() {
		return LinkSource.BASIC;
	}

	@Override
	public CrawledDto crawl(String link) throws IOException {
		Document target = scrap(link);
		String title = fetchMeta(target, "title");
		String description = fetchMeta(target, "description");
		String image = fetchImage(target, link);
		return new CrawledDto(title, link, image, description);
	}

	private Document scrap(String link) throws IOException {
		return Jsoup.connect(link)
			.timeout(TIMEOUT_MILLISECONDS)
			.get();
	}

	private String fetchMeta(Document target, String tag) {
		Element element = target.selectFirst("meta[property=og:%s]".formatted(tag));
		if (element == null) {
			if (tag.equals("title")) {
				return "저의 대표프로젝트를 소개할게요";
			} else if (tag.equals("description")) {
				return "썸네일을 눌러서 상세하게 살펴보세요";
			}
			return EMPTY_STRING;
		}
		return element.attr("content");
	}

	private String fetchImage(Document target, String link) {
		String image = fetchMeta(target, "image");
		if (!image.contains("http")) {
			return fetchBaseUrl(link) + image;
		}
		return image;
	}

	private String fetchBaseUrl(String link) {
		try {
			URL url = new URL(link);
			return url.getProtocol() + DELIMITER_OF_BASE_URL + url.getHost();
		} catch (MalformedURLException ex) {
			throw new TookException(CardErrorCode.INVALID_CRAWL_URL);
		}
	}
}
