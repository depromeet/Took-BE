package com.evenly.took.feature.card.client;

import java.io.IOException;

import com.evenly.took.feature.card.client.dto.CrawledDto;

public interface LinkCrawler {

	LinkSource supportSource();

	CrawledDto crawl(String link) throws IOException;
}
