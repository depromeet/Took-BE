package com.evenly.took.feature.card.client.crawler;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import io.github.bonigarcia.wdm.WebDriverManager;

@Component
public class WebDriverHandler {

	private static final ChromeOptions OPTIONS = new ChromeOptions()
		.addArguments("--headless")
		.addArguments("--disable-gpu")
		.addArguments("--no-sandbox");

	public WebDriver fetch() {
		WebDriverManager.chromedriver().setup();
		return new ChromeDriver(OPTIONS);
	}

	public void quit(WebDriver webDriver) {
		if (webDriver == null) {
			return;
		}
		webDriver.quit();
	}
}
