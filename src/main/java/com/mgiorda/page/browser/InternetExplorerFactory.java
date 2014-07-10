package com.mgiorda.page.browser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.mgiorda.context.SpringUtil;

public class InternetExplorerFactory extends AbstractBrowserFactory {

	private static final Log logger = LogFactory.getLog(InternetExplorerFactory.class);

	private boolean deleteCookiesAtStart = false;

	public InternetExplorerFactory(String driverProperty) {

		File resourceFile = SpringUtil.getCreateClasspathFile(driverProperty);
		System.setProperty("webdriver.ie.driver", resourceFile.getAbsolutePath());
	}

	@Override
	public WebDriver newDriver() {

		DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
		ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		ieCapabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
		ieCapabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
		ieCapabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);

		WebDriver driver = new InternetExplorerDriver(ieCapabilities);

		if (deleteCookiesAtStart) {
			try {
				Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 255");

			} catch (IOException e) {
				logger.warn(e);
			}
		}

		return driver;
	}

	public boolean isDeleteCookiesAtStart() {
		return deleteCookiesAtStart;
	}

	public void setDeleteCookiesAtStart(boolean deleteCookiesAtStart) {
		this.deleteCookiesAtStart = deleteCookiesAtStart;
	}

}
