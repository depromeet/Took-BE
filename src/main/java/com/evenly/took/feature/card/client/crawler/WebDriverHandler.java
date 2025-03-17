package com.evenly.took.feature.card.client.crawler;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import io.github.bonigarcia.wdm.WebDriverManager;

@Component
public class WebDriverHandler {

	public WebDriver fetch() {
		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");

		return new ChromeDriver(options);
	}

	public void quit(WebDriver webDriver) {
		if (webDriver == null) {
			return;
		}
		webDriver.quit();
	}
}
