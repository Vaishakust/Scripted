package com.scripted.runners;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.scripted.configurations.SkriptmateConfigurations;
import com.scripted.web.BrowserDriver;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@SuppressWarnings("unused")
@CucumberOptions(features = "Features/Webservices", plugin = { "json:target/cucumber.json","html:target/cucumber.html" , "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"}, glue = { "com.scripted.api.apistepdefs" }, monochrome = true, tags = "@demo_xml_api")
public class APIRunner extends AbstractTestNGCucumberTests {
	@BeforeSuite
	public void setup() {
		SkriptmateConfigurations.beforeSuite();
	}

	@AfterSuite
	public void teardown() {
		SkriptmateConfigurations.afterSuite();
		
	}
}