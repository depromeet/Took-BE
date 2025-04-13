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
public class BrunchLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";

	@Override
	public LinkSource supportSource() {
		return LinkSource.BRUNCH;
	}

	@Override
	public CrawledDto crawl(String link) throws IOException {
		Document target = scrap(link);
		String title = fetchMeta(target, "title");
		String url = fetchMeta(target, "url");
		String image = "https:" + fetchMeta(target, "image");
		String description = fetchMeta(target, "description");
		return new CrawledDto(title, url, image, description);
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
}
