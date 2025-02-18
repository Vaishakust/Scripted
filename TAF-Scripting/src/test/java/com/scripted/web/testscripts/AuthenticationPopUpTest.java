package com.scripted.web.testscripts;

import org.openqa.selenium.WebDriver;

import com.scripted.generic.GenericUtils;
import com.scripted.web.BrowserDriver;

public class AuthenticationPopUpTest {
	public static WebDriver driver = BrowserDriver.funcGetWebdriver();

	public static void main(String[] args) throws Exception {
		//String epswd = GenericUtils.encryptPass("admin");
		BrowserDriver.launchWebURL_Auth("http://the-internet.herokuapp.com/basic_auth", "admin", "admin");
		Thread.sleep(30000);
	}

}


