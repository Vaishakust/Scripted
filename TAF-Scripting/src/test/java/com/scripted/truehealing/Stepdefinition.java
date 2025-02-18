package com.scripted.truehealing;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import io.cucumber.java.Scenario;
import com.scripted.Allure.AllureListener;
import com.scripted.dataload.PropertyDriver;
import com.scripted.web.BrowserDriver;
 
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Allure;

public class Stepdefinition {
	NewPage newpage;
	Homepage newpage1;
	
	WebDriver driver;
	 @Before("@localhost")
	    public void invokeBrowser(Scenario scenario) throws Exception {
	    }
	
	
	    @After("@localhost")
	    public void afterScenario(Scenario scenario) {
	        System.out.println("-----------------------------------------");
	        System.out.println("Completed Scenario with tags -> ");
	        BrowserDriver.getDriver().quit();
	    }
	
	
	@Given("I launch the application for localhost")
	public void saveMART() throws Throwable {
		//BrowserDriver.funcGetNgWebdriver();
		BrowserDriver.getCuPalDriver();
		driver =BrowserDriver.getDriver();
		AllureListener.setDriver(BrowserDriver.getDriver());
		newpage = PageFactory.initElements(BrowserDriver.getDriver(), NewPage.class);
		newpage1 = PageFactory.initElements(BrowserDriver.getDriver(), Homepage.class);
		
		BrowserDriver.launchWebURL("http://localhost:8080/JenkinsProjectRel1/Sample/src/main/webapp/basic-first-form-demo.html");
//		BrowserDriver.launchWebURL(
//				"http://localhost:8080/JenkinsProjectRel1/Sample/src/main/webapp/table-pagination-demo.html");
		Thread.sleep(4000);
	}
	
	@Then("I enter the functionalities")
	public void function1() throws Throwable {
		//BrowserDriver.funcGetNgWebdriver();
		newpage.entervalueandClick();
//		newpage1.entervalueandClick();
	}
	

	@Then("I check the table details")
	public void tableDetails() throws Throwable {
		//BrowserDriver.funcGetNgWebdriver();
		BrowserDriver.getDriver().navigate().to("http://localhost:8080/JenkinsProjectRel1/Sample/src/main/webapp/table-pagination-demo.html");
		Thread.sleep(4000);
		newpage.tableCheck();
	}
	
	
	@Given("I launch the application for workday")
	public void workDay() throws Throwable {
		//BrowserDriver.funcGetNgWebdriver();
		BrowserDriver.getCuPalDriver();
		driver =BrowserDriver.getDriver();
		AllureListener.setDriver(BrowserDriver.getDriver());
		newpage = PageFactory.initElements(BrowserDriver.getDriver(), NewPage.class);
		BrowserDriver.launchWebURL("https://wd5-impl.workday.com/wday/authgwy/ust2/login.htmld?redirect=n");
		Thread.sleep(4000);
	}
	
	@Then("I enter the login credentials for workday")
	public void loginCredentials() throws Throwable {
		//BrowserDriver.funcGetNgWebdriver();
		newpage.workDayLogin();
	}
	
	
	

}
