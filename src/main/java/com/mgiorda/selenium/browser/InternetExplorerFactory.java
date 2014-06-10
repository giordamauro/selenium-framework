package com.mgiorda.selenium.browser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.mgiorda.testng.BrowserFactory;

public class InternetExplorerFactory implements BrowserFactory {

	private static final Log logger = LogFactory.getLog(InternetExplorerFactory.class);

	public InternetExplorerFactory(String driverProperty) {

		@SuppressWarnings("resource")
		ApplicationContext appContext = new ClassPathXmlApplicationContext();
		Resource resource = appContext.getResource(driverProperty);

		try {
			File resourceFile = resource.getFile();
			System.setProperty("webdriver.ie.driver", resourceFile.getAbsolutePath());

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public WebDriver newDriver() {
		DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
		WebDriver driver = new InternetExplorerDriver(ieCapabilities);

		try {
			Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 255");

		} catch (IOException e) {
			logger.warn(e);
		}

		return driver;
	}

}
