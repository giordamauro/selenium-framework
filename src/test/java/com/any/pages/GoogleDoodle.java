package com.any.pages;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;
import com.mgiorda.page.element.Link;

public class GoogleDoodle extends AbstractElement {

	@Locate(@By(xpath = "./a"))
	private Link link;

	public void click() {
		link.click();
	}
}