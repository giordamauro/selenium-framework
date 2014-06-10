package com.mgiorda.selenium.browser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.mgiorda.testng.BrowserFactory;

public class ChromeFactory implements BrowserFactory {

	public ChromeFactory(Map<OperativeSystem, String> driverPropertiesByOS) {

		if (driverPropertiesByOS == null) {
			throw new IllegalArgumentException("DriverPropertiesByOS cannot be null");
		}

		OperativeSystem currentOS = OperativeSystem.getCurrentOS();
		String driverProperty = driverPropertiesByOS.get(currentOS);

		if (driverProperty == null) {
			throw new IllegalStateException(String.format("Couldn't found chromeDriverProperty for current OperativeSystem: %s", currentOS));
		}

		@SuppressWarnings("resource")
		ApplicationContext appContext = new ClassPathXmlApplicationContext();
		Resource resource = appContext.getResource(driverProperty);

		try {
			File resourceFile = resource.getFile();
			System.setProperty("webdriver.chrome.driver", resourceFile.getAbsolutePath());

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public WebDriver newDriver() {
		DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
		WebDriver driver = new ChromeDriver(chromeCapabilities);

		return driver;
	}

}
