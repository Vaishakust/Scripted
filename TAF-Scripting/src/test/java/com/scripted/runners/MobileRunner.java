package com.scripted.runners;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.scripted.configurations.SkriptmateConfigurations;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "Features/Mobile", plugin = { "json:target/cucumber.json", "html:target/cucumber.html",
		"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:" }, glue = {
				"com.scripted.mobiletestcases" }, monochrome = true, tags = "@pCloudyWebtest")
public class MobileRunner extends AbstractTestNGCucumberTests {
	@BeforeSuite
	public void setup() {
		SkriptmateConfigurations.beforeSuite();
	}

	@AfterSuite
	public void teardown() {
		SkriptmateConfigurations.afterSuite();
	}
}
