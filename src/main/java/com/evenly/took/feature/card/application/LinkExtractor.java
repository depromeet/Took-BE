package com.evenly.took.feature.card.application;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.CrawlerComposite;
import com.evenly.took.feature.card.client.LinkSource;
import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkExtractor {

	private final CrawlerComposite crawlerComposite;

	public CrawledDto extractLink(String link) {
		try {
			LinkSource source = LinkSource.parseSource(link);
			return crawlerComposite.crawl(source, link);
		} catch (Exception ex) {
			log.error("크롤링에 실패하였습니다: ", ex);
			throw new TookException(CardErrorCode.CANNOT_CRAWL);
		}
	}
}
