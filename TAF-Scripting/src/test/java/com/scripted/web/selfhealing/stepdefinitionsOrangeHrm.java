package com.scripted.web.selfhealing;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.scripted.accessibility.main.Accessibility;
import com.scripted.dataload.ExcelConnector;
import com.scripted.dataload.PropertyDriver;
import com.scripted.dataload.TestDataFactory;
import com.scripted.dataload.TestDataObject;
import com.scripted.generic.FileUtils;
import com.scripted.pageobjects.NewPage1;
import com.scripted.pageobjects.NewPageOrangeHrm;
import com.scripted.web.BrowserDriver;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class stepdefinitionsOrangeHrm {

	WebDriver driver;
	NewPageOrangeHrm objLoginPage;
	static Accessibility access;
	public static Map<String, String> fAutoRow;
	PropertyDriver propDriver = new PropertyDriver();

	@Before("@Excel")
	public void setupExcelData() {
		propDriver.setPropFilePath("src/main/resources/DataFiles/Properties/config.properties");
		String filename = FileUtils.getCurrentDir() + PropertyDriver.readProp("DataSheetName");
		String sheetname = "TC001";
		String key = "";
		ExcelConnector con = new ExcelConnector(filename, sheetname);
		TestDataFactory test = new TestDataFactory();
		TestDataObject obj = test.GetData(key, con);
		LinkedHashMap<String, Map<String, String>> getAllData = obj.getTableData();
		fAutoRow = (getAllData.get("1"));

	}

	@Before("@OrangeHrm")
	public void invokeBrowser() {
		BrowserDriver.getCuPalDriver();
		driver = BrowserDriver.getDriver();
		access = new Accessibility(driver);
	}

	@After("@OrangeHrm")
	public void after() {
// AccessibilityReportEngine reportEngine = new AccessibilityReportEngine();
// reportEngine.generateOverallAccessibilityReports();
		BrowserDriver.closeBrowser();
	}

	@Given("user launch the application for OrangeHrm")
	public void saveMART1() throws Throwable {

		objLoginPage = PageFactory.initElements(BrowserDriver.getDriver(), NewPageOrangeHrm.class);
		BrowserDriver.launchWebURL("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

	}

	@Then("user login to the application for OrangeHrm")
	public void user_pass() throws Throwable {
		// PerfAPIClient.doPerfTest("Home Page", driver.getCurrentUrl());
		// access.generateAccessibilityReportForCurrentPage(driver.getCurrentUrl());
		objLoginPage.login("Admin", "admin123");

	}
}
