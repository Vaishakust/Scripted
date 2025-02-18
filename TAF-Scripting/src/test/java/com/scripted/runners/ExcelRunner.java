package com.scripted.runners;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.scripted.configurations.SkriptmateConfigurations;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "Features/Excel", plugin = { "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm","json:target/cucumber.json","html:target/cucumber.html" }, glue = { "com.scripted.excel" }, monochrome = true, tags = "@ColumnWise")
public class ExcelRunner extends AbstractTestNGCucumberTests {
	@BeforeSuite
	public void setup() {

		SkriptmateConfigurations.beforeSuite();
	}

	@AfterSuite
	public void teardown() {
		SkriptmateConfigurations.afterSuite();
	}
}
