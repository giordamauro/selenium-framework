package com.mgiorda.selenium.browser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.mgiorda.selenium.BrowserFactory;

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

		File chromeFile = new File(driverProperty);
		if (!chromeFile.exists()) {

			@SuppressWarnings("resource")
			ApplicationContext appContext = new ClassPathXmlApplicationContext();
			Resource resource = appContext.getResource("classpath:" + driverProperty);

			try {

				chromeFile.getParentFile().mkdirs();
				chromeFile.createNewFile();
				InputStream inputStream = resource.getInputStream();

				FileOutputStream outputStream = new FileOutputStream(chromeFile);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				outputStream.close();

			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		System.setProperty("webdriver.chrome.driver", chromeFile.getAbsolutePath());
	}

	@Override
	public WebDriver newDriver() {
		DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
		WebDriver driver = new ChromeDriver(chromeCapabilities);

		return driver;
	}

}
