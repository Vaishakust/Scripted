package com.scripted.web.testscripts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.scripted.brokenlink.BrokenLink;
import com.scripted.web.BrowserDriver;

public class BrokenLinkTest {
	public static WebDriver driver = BrowserDriver.funcGetWebdriver();
	public static void main(String[] args) {
		BrokenLink brokenlink=new BrokenLink(BrowserDriver.getDriver());
		BrowserDriver.launchWebURL("https://www.citroen.in/");
		BrowserDriver.getDriver().findElement(By.xpath("/html/body/ngb-modal-window/div/div/div[1]/a")).click();
		BrowserDriver.pageWait();
		brokenlink.brokenLinkValidate("CIT_HomePage");
		BrowserDriver.getDriver().findElement(By.xpath("//*[@id='iconLocatorTop']")).click();
		BrowserDriver.pageWait();
		brokenlink.brokenLinkValidate("CIT_HelpPage");
		brokenlink.brokenLinkReportGen();
		BrowserDriver.quitBrowser();
			}

}
