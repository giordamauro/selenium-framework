package com.any.pages;

import java.util.List;

import com.mgiorda.page.AbstractElement;
import com.mgiorda.page.annotations.By;
import com.mgiorda.page.annotations.Locate;
import com.mgiorda.page.element.Link;

public class FooterBar extends AbstractElement {

	@Locate(@By(xpath = "./div/span"))
	private List<Link> links;

}
