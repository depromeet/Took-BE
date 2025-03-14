package com.evenly.took.feature.card.client;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.crawler.BasicLinkCrawler;
import com.evenly.took.feature.card.client.dto.CrawledDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CrawlerComposite {

	private final Map<LinkSource, LinkCrawler> mapping;

	public CrawlerComposite(Set<LinkCrawler> crawlers) {
		this.mapping = crawlers.stream()
			.collect(Collectors.toUnmodifiableMap(LinkCrawler::supportSource, Function.identity()));
	}

	public CrawledDto crawl(LinkSource source, String link) throws IOException {
		return Optional.ofNullable(mapping.get(source))
			.orElseGet(BasicLinkCrawler::new)
			.crawl(link);
	}
}
