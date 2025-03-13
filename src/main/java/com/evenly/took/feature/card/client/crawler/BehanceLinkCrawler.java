package com.evenly.took.feature.card.client.crawler;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.client.LinkCrawler;
import com.evenly.took.feature.card.client.LinkSource;
import com.evenly.took.feature.card.client.dto.CrawledDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class BehanceLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";

	private final WebDriver driver;

	@Override
	public LinkSource supportSource() {
		return LinkSource.BEHANCE;
	}

	@Override
	public CrawledDto crawl(String link) {
		try {
			scrap(link);
			String title = fetchMeta("title");
			String url = fetchMeta("url");
			String image = fetchMeta("image");
			String description = fetchMeta("description");
			return new CrawledDto(title, url, image, description);
		} catch (Exception ex) {
			log.error("비핸스 크롤링에 실패하였습니다.");
			throw ex;
		} finally {
			driver.quit();
		}
	}

	private void scrap(String link) {
		driver.get(link);
		waitUntilLoad();
	}

	private void waitUntilLoad() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(TIMEOUT_MILLISECONDS));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("meta[property='og:title']")));
	}

	private String fetchMeta(String tag) {
		String selector = "meta[property='og:%s']".formatted(tag);
		try {
			return driver.findElement(By.cssSelector(selector)).getAttribute("content");
		} catch (NoSuchElementException ex) {
			return EMPTY_STRING;
		}
	}
}
