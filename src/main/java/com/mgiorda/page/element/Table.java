package com.mgiorda.page.element;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mgiorda.annotation.By;
import com.mgiorda.annotation.Locate;
import com.mgiorda.test.AbstractElement;

public class Table extends AbstractElement {

	@Locate(@By(tagName = "tr"))
	private List<PageElement> rows;

	public Table(PageElement pageElement) {
		super(pageElement);
	}

	// TODO createSubElements generics
	public List<List<PageElement>> getRows() {

		for (PageElement row : rows) {
			List<WebElement> columns = row.findElements(By.tagName("td"));
			String columnText = new String(columns.get(0).getText());
			if (columnText.contains(blacklistedTerm.toLowerCase())) {
				columns.get(2).findElement(By.tagName("a")).click();
				WebDriverWait wait = new WebDriverWait(driver, 10);
				wait.until(ExpectedConditions.elementToBeClickable(DELETETERMBUTTON)).click();
				exitLoop = false;
			}
		}
	}
}
