package com.mgiorda.selenium.browser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.mgiorda.commons.ClasspathUtil;
import com.mgiorda.selenium.BrowserFactory;

public class InternetExplorerFactory implements BrowserFactory {

	private static final Log logger = LogFactory.getLog(InternetExplorerFactory.class);

	public InternetExplorerFactory(String driverProperty) {

		File resourceFile = ClasspathUtil.getClasspathFile(driverProperty);
		System.setProperty("webdriver.ie.driver", resourceFile.getAbsolutePath());
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
