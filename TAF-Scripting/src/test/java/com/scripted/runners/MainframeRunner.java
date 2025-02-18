package com.scripted.runners;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import com.scripted.configurations.SkriptmateConfigurations;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "Features/Mainframe", plugin = { "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:","json:target/cucumber.json","html:target/cucumber.html" }, glue = { "com.scripted.mainframe" }, monochrome = true, tags = "@Polar")
public class MainframeRunner extends AbstractTestNGCucumberTests {
	@BeforeSuite
	public void setup() {
		SkriptmateConfigurations.beforeSuite();
	}

	@AfterSuite
	public void teardown() {
		SkriptmateConfigurations.afterSuite();		
	} 
}
