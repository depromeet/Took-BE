package com.evenly.took.feature.card.client.crawler;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebDriverManager {

	@Value("${crawling.chrome-binary}")
	private String chromeBinary;

	public WebDriver fetch() {
		io.github.bonigarcia.wdm.WebDriverManager.chromedriver().clearDriverCache().setup();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");

		if (!chromeBinary.isEmpty()) {
			options.setBinary(chromeBinary);
		}

		return new ChromeDriver(options);
	}

	public void quit(WebDriver webDriver) {
		webDriver.quit();
	}
}
