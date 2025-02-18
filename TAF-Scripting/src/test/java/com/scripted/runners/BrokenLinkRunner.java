package com.scripted.runners;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.scripted.configurations.SkriptmateConfigurations;
import com.scripted.web.BrowserDriver;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "Features/BrokenLink", plugin = { "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm","json:target/cucumber.json","com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:","html:target/cucumber.html" }, glue = { "com.scripted.brokenlink" }, monochrome = true, tags = "@ValidateResponse")
public class BrokenLinkRunner extends AbstractTestNGCucumberTests {
	@BeforeSuite
	public void setup() {

		SkriptmateConfigurations.beforeSuite();
	}

	@AfterSuite
	public void teardown() {
		SkriptmateConfigurations.afterSuite();
	}
}