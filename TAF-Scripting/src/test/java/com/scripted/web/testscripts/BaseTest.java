package com.scripted.web.testscripts;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.scripted.configurations.SkriptmateConfigurations;
import com.scripted.web.BrowserDriver;

@SuppressWarnings("unused")
public class BaseTest {
	
	@BeforeSuite
	public void setup() {
		SkriptmateConfigurations.beforeSuite();
	}

	@AfterSuite
	public void teardown()  {
		
	SkriptmateConfigurations.afterSuite();
//	BrowserDriver.getDriver().close();
	}

}
