package com.scripted.runners;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.scripted.configurations.SkriptmateConfigurations;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "Features/Database", plugin = { "json:target/cucumber.json","html:target/cucumber.html" }, glue = { "com.scripted.database" }, monochrome = true, tags = "@MongoDB")
public class DataBaseRunner extends AbstractTestNGCucumberTests {
	@BeforeSuite
	public void setup() {
		SkriptmateConfigurations.beforeSuite();
	}

	@AfterSuite
	public void teardown() {
		SkriptmateConfigurations.afterSuite();
		
	}

}
