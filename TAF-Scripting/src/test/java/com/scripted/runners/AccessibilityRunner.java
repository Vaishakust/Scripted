package com.scripted.runners;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.scripted.configurations.SkriptmateConfigurations;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "Features/Accessibility", plugin = {"json:target/cucumber.json","html:target/cucumber.html" }, glue = { "com.scripted.accessibility" }, monochrome = true, tags = "@AccessibilityTest")
public class AccessibilityRunner extends AbstractTestNGCucumberTests {
	@BeforeSuite
	public void setup() {
		SkriptmateConfigurations.beforeSuite();
	}

	@AfterSuite
	public void teardown() {
		SkriptmateConfigurations.afterSuite();
		
	}
}
