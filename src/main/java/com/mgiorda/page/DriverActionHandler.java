package com.mgiorda.page;

public interface DriverActionHandler {

	String getTitle();

	String getUrl();

	void quit();

	void goToUrl(String url);

	void waitForPageToLoad();

	void takeScreenShot(String filePath);

}