package com.scripted.web.testscripts;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.scripted.web.WebHandlers;

public class Gmail {
WebDriver driver;
	
	@FindBy(xpath ="//*[@target='_top']")
	private WebElement gmail;
	@FindBy(xpath ="//*[@target='_top']1")
	private WebElement gmail1;

	public Gmail(WebDriver driver) {
		
		this.driver = driver;
		
	}
	
	public void Click() throws Exception {
		WebHandlers.click(gmail);
		
	}
	
	public void WrongClick() {
		WebHandlers.click(gmail1);
	}
	

}
