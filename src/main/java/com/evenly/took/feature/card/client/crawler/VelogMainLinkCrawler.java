package com.evenly.took.feature.card.client.crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.LinkCrawler;
import com.evenly.took.feature.card.client.LinkSource;
import com.evenly.took.feature.card.client.dto.CrawledDto;

@Component
public class VelogMainLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";

	@Override
	public LinkSource supportSource() {
		return LinkSource.VELOG_MAIN;
	}

	@Override
	public CrawledDto crawl(String link) throws IOException {
		Document document = scrap(link);
		String title = extractTitle(document);
		String image = extractImage(document);
		String description = extractDescription(document);
		return new CrawledDto(title, link, image, description);
	}

	private Document scrap(String link) throws IOException {
		return Jsoup.connect(link)
			.timeout(TIMEOUT_MILLISECONDS)
			.get();
	}

	private String extractTitle(Document document) {
		String title = document.head().getElementsByTag("title").text();
		return title.isEmpty() ? "저의 대표프로젝트를 소개할게요" : title;
	}

	private String extractImage(Document document) {
		Element image = document.selectFirst(
			"body > div > div:nth-child(2) > div:nth-child(2) > main > div > div:nth-child(1) > div:nth-child(1) > div > a > img");
		if (image == null) {
			return EMPTY_STRING;
		}
		return image.attr("src");
	}

	private String extractDescription(Document document) {
		Element description = document.selectFirst(
			"body > div > div:nth-child(2) > div:nth-child(2) > main > div > div:nth-child(1) > div:nth-child(1) > div > div > div:nth-child(2)");
		if (description == null) {
			return "썸네일을 눌러서 상세하게 살펴보세요";
		}
		return description.text();
	}
}
