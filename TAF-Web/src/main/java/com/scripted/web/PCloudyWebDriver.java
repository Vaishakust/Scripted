package com.scripted.web;

import java.net.URL;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import com.scripted.dataload.PropertyDriver;

public class PCloudyWebDriver {
	public static WebDriver driver;
	public static String strBrowserName = null;
	public static String strBrowserVersion = null;
	public static String strPlatform = null;
	public static String strPlatformVersion = null;
	public static String strClientName = null;
	public static String strPcloudyAccessKey = null;
	public static String strPcloudyURL = null;
	public static String strEmail = null;

	public static Logger LOGGER = LogManager.getLogger(PCloudyWebDriver.class);

	public static WebDriver funcPcloudyWebdriver() {
		try {
			PropertyDriver pcWebConfig = new PropertyDriver();
			pcWebConfig.setPropFilePath("src/main/resources/Web/Properties/pCloudy/Web.properties");
			strBrowserName = PropertyDriver.readProp("browserName");
			
			strBrowserVersion = PropertyDriver.readProp("browserversion");
			strClientName = PropertyDriver.readProp("Clientname");
			strPcloudyAccessKey = PropertyDriver.readProp("apiKey");
			strEmail = PropertyDriver.readProp("Email");
			strPcloudyURL = PropertyDriver.readProp("PcloudyURL");
			/*if (!(pcWebConfig.readProp("httpsproxyHost").isEmpty()) && !(pcWebConfig.readProp("httpsproxyPort").isEmpty())) {
				System.setProperty("https.proxyHost", pcWebConfig.readProp("httpsproxyHost"));
				System.setProperty("https.proxyPort", pcWebConfig.readProp("httpsproxyPort"));
			}*/

			if (strBrowserName == null || strBrowserName.equals(" ")) {
				LOGGER.info("Browser name is null, please check the value of browserName in config.properties");
				System.exit(0);
			}else {
				LOGGER.info("Browser : " + strBrowserName);
				if (strBrowserName.equals("chrome")) {
					ChromeOptions dcap = new ChromeOptions();
					dcap.setCapability("os", strPlatform);
					dcap.setCapability("osVersion", strPlatformVersion);
					dcap.setCapability("browserVersion", strBrowserVersion);
					dcap.setCapability("clientName", strClientName);
					dcap.setCapability("apiKey", strPcloudyAccessKey);
					dcap.setCapability("email", strEmail);
					driver = new RemoteWebDriver(new URL(strPcloudyURL), dcap);
					
				} else if (strBrowserName.equals("ie")) {
					InternetExplorerOptions dcap = new InternetExplorerOptions();
					dcap.setCapability("os", strPlatform);
					dcap.setCapability("osVersion", strPlatformVersion);
					dcap.setCapability("browserVersion", strBrowserVersion);
					dcap.setCapability("clientName", strClientName);
					dcap.setCapability("apiKey", strPcloudyAccessKey);
					dcap.setCapability("email", strEmail);
					driver = new RemoteWebDriver(new URL(strPcloudyURL), dcap);
				} else if (strBrowserName.equals("firefox")) {
					
					FirefoxOptions dcap = new FirefoxOptions();
					dcap.setCapability("os", strPlatform);
					dcap.setCapability("osVersion", strPlatformVersion);
					dcap.setCapability("browserVersion", strBrowserVersion);
					dcap.setCapability("clientName", strClientName);
					dcap.setCapability("apiKey", strPcloudyAccessKey);
					dcap.setCapability("email", strEmail);
					driver = new RemoteWebDriver(new URL(strPcloudyURL), dcap);
				} else if (strBrowserName.equals("edge")) {			
					EdgeOptions dcap = new EdgeOptions();
					dcap.setCapability("os", strPlatform);
					dcap.setCapability("osVersion", strPlatformVersion);
					dcap.setCapability("browserVersion", strBrowserVersion);
					dcap.setCapability("clientName", strClientName);
					dcap.setCapability("apiKey", strPcloudyAccessKey);
					dcap.setCapability("email", strEmail);
					driver = new RemoteWebDriver(new URL(strPcloudyURL), dcap);
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error occurred while configuring webdrivers" + "Exception :" + e);
			Assert.fail("Webdriver initialisation issues" + "Exception :" + e);
		}
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		BrowserDriver.driver = driver;
		return driver;
	}
}
