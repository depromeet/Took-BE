package com.evenly.took.feature.card.client;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.crawler.BasicLinkCrawler;
import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CrawlerComposite {

	private final Map<LinkSource, LinkCrawler> mapping;

	public CrawlerComposite(Set<LinkCrawler> crawlers) {
		this.mapping = crawlers.stream()
			.collect(Collectors.toUnmodifiableMap(LinkCrawler::supportSource, Function.identity()));
	}

	public CrawledDto crawl(LinkSource source, String link) {
		try {
			return Optional.ofNullable(mapping.get(source))
				.orElseGet(BasicLinkCrawler::new)
				.crawl(link);
		} catch (Exception ex) {
			log.error("크롤링에 실패하였습니다: ", ex);
			throw new TookException(CardErrorCode.CANNOT_CRAWL);
		}
	}
}
