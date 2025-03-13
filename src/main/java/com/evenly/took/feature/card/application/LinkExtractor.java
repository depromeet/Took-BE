package com.evenly.took.feature.card.application;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.CrawlerComposite;
import com.evenly.took.feature.card.client.LinkSource;
import com.evenly.took.feature.card.client.dto.CrawledDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LinkExtractor {

	private final CrawlerComposite crawlerComposite;

	public CrawledDto extractLink(String rawSource, String link) {
		LinkSource source = LinkSource.asLinkSource(rawSource);
		return crawlerComposite.crawl(source, link);
	}
}
