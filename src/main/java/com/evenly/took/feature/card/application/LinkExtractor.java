package com.evenly.took.feature.card.application;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.CrawledType;
import com.evenly.took.feature.card.client.CrawlerComposite;
import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LinkExtractor {

	private final CrawlerComposite crawlerComposite;

	public Content extractContent(String link) {
		CrawledType type = CrawledType.asType(link);
		CrawledDto result = crawlerComposite.crawl(type, link);
		return result.toContent();
	}

	public Project extractProject(String link) {
		CrawledType type = CrawledType.asType(link);
		CrawledDto result = crawlerComposite.crawl(type, link);
		return result.toProject();
	}
}
