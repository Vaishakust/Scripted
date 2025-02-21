package com.scripted.web;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.Assert;

public class MsEdgeSettings {
	private EdgeOptions edgeOptionsObj = new EdgeOptions();
	public static Logger LOGGER = LogManager.getLogger(MsEdgeSettings.class);
	public EdgeOptions setByMsEdgeOptions(File fileName) {
		try {
			System.setProperty("webdriver.edge.driver", WebDriverPathUtil.getMsEdgeDriverPath());
			return edgeOptionsObj;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error occurred while configuring Edge browser , Exception: " + e);
			Assert.fail("Error occurred while configuring Edge browser , Exception: " + e);
		}
		return edgeOptionsObj;
	}
}
