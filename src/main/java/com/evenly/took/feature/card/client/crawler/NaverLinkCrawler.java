package com.evenly.took.feature.card.client.crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.LinkCrawler;
import com.evenly.took.feature.card.client.LinkSource;
import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NaverLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";
	private static final String PREFIX_OF_IFRAME = "https://blog.naver.com";

	@Override
	public LinkSource supportSource() {
		return LinkSource.NAVER;
	}

	@Override
	public CrawledDto crawl(String link) throws IOException {
		Document target = scrap(link);
		Document iframe = scrapIframe(target);
		String title = fetchMeta(iframe, "title");
		String url = fetchMeta(iframe, "url");
		String image = fetchMeta(iframe, "image");
		String description = fetchMeta(iframe, "description");
		return new CrawledDto(title, url, image, description);
	}

	private Document scrap(String link) throws IOException {
		return Jsoup.connect(link)
			.timeout(TIMEOUT_MILLISECONDS)
			.get();
	}

	private Document scrapIframe(Document target) throws IOException {
		Element iframe = target.selectFirst("iframe");
		if (iframe == null) {
			log.error("네이버 블로그 iframe이 존재하지 않습니다.");
			throw new TookException(CardErrorCode.CANNOT_CRAWL);
		}
		String iframeUrl = iframe.attr("src");
		return scrap(PREFIX_OF_IFRAME + iframeUrl);
	}

	private String fetchMeta(Document iframe, String tag) {
		Element element = iframe.selectFirst("meta[property=og:%s]".formatted(tag));
		if (element == null) {
			return EMPTY_STRING;
		}
		return element.attr("content");
	}
}
