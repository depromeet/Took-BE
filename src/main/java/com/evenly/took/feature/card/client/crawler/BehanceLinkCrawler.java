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
@Component
@RequiredArgsConstructor
public class BehanceLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";

	private final WebDriverManager webDriverManager;

	@Override
	public LinkSource supportSource() {
		return LinkSource.BEHANCE;
	}

	@Override
	public CrawledDto crawl(String link) {
		WebDriver driver = webDriverManager.fetch();
		try {
			scrap(driver, link);
			String title = fetchMeta(driver, "title");
			String url = fetchMeta(driver, "url");
			String image = fetchMeta(driver, "image");
			String description = fetchMeta(driver, "description");
			return new CrawledDto(title, url, image, description);
		} catch (Exception ex) {
			log.error("비핸스 크롤링에 실패하였습니다.");
			throw ex;
		} finally {
			webDriverManager.quit(driver);
		}
	}

	private void scrap(WebDriver driver, String link) {
		driver.get(link);
		waitUntilLoad(driver);
	}

	private void waitUntilLoad(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(TIMEOUT_MILLISECONDS));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("meta[property='og:title']")));
	}

	private String fetchMeta(WebDriver driver, String tag) {
		String selector = "meta[property='og:%s']".formatted(tag);
		try {
			return driver.findElement(By.cssSelector(selector)).getAttribute("content");
		} catch (NoSuchElementException ex) {
			return EMPTY_STRING;
		}
	}
}
