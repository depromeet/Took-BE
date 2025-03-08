package com.evenly.took.feature.card.client.crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.CrawledType;
import com.evenly.took.feature.card.client.LinkCrawler;
import com.evenly.took.feature.card.client.dto.CrawledDto;

@Component
public class BrunchLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";

	@Override
	public CrawledType supportType() {
		return CrawledType.BRUNCH;
	}

	@Override
	public CrawledDto crawl(String link) throws IOException {
		String html = scrap(link);
		Document document = Jsoup.parse(html);
		Element head = document.head();
		Elements meta = head.getElementsByTag("meta");
		return new CrawledDto(title(meta), link(meta), image(meta), description(meta));
	}

	private String scrap(String link) throws IOException {
		return Jsoup.connect(link)
			.timeout(TIMEOUT_MILLISECONDS)
			.get()
			.html();
	}

	private String title(Elements meta) {
		return meta.stream()
			.filter(element -> element.hasAttr("property"))
			.filter(element -> element.attribute("property").getValue().equals("og:title"))
			.map(element -> element.attribute("content").getValue())
			.findAny()
			.orElse(EMPTY_STRING);
	}

	private String link(Elements meta) {
		return meta.stream()
			.filter(element -> element.hasAttr("property"))
			.filter(element -> element.attribute("property").getValue().equals("og:link"))
			.map(element -> element.attribute("content").getValue())
			.findAny()
			.orElse(EMPTY_STRING);
	}

	private String image(Elements meta) {
		return meta.stream()
			.filter(element -> element.hasAttr("property"))
			.filter(element -> element.attribute("property").getValue().equals("og:image"))
			.map(element -> "https://" + element.attribute("content").getValue())
			.findAny()
			.orElse(EMPTY_STRING);
	}

	private String description(Elements meta) {
		return meta.stream()
			.filter(element -> element.hasAttr("property"))
			.filter(element -> element.attribute("property").getValue().equals("og:description"))
			.map(element -> element.attribute("content").getValue())
			.findAny()
			.orElse(EMPTY_STRING);
	}
}
