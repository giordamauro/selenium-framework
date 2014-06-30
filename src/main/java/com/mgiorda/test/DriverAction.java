package com.mgiorda.test;

public interface DriverAction {

	String getTitle();

	String getUrl();

	void quit();

	void goToUrl(AbstractPage page, String url);

	void takeScreenShot(String filePath);

}