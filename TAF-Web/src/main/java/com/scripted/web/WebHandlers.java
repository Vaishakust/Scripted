package com.scripted.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.winium.DesktopOptions;
import org.openqa.selenium.winium.WiniumDriver;
import org.openqa.selenium.winium.WiniumDriverService;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.paulhammant.ngwebdriver.ByAngular;
import com.scripted.accessibility.main.Accessibility;
import com.scripted.accessibility.reporting.AccessibilityReportEngine;
import com.scripted.dataload.PropertyDriver;
import com.scripted.generic.FileUtils;
import com.scripted.selfhealing.SMWebHealer;

public class WebHandlers {
//	public static Actions action = new Actions(BrowserDriver.getDriver());
	public static Logger LOGGER = LogManager.getLogger(WebHandlers.class);
	public static WiniumDriver desktopDriver = null;
	public static WebDriver driver;
	public String technology = "web";
	static WebElement HealdLctr = null;
	static WebElement elementBasedonText = null;
	static String cdir = System.getProperty("user.dir");
	static SMWebHealer smhealer = new SMWebHealer();
//	public static ATUTestRecorder recorder;
	static List<String> headerDetails = new ArrayList<String>();
	static List<String> spanDetails = new ArrayList<String>();
	static List<String> paraDetails = new ArrayList<String>();
	static HashMap<String, WebElement> Elementbelow = new HashMap<>();
	static HashMap<String, WebElement> Elementabove = new HashMap<>();
	public static HashMap<String, WebElement> ParentElementforHealing = new HashMap<>();
	static Boolean elementTextvalue = false;
	static Boolean InsideFrame = false;

	/**
	 * Converts By locator to Web Element
	 * @param driver WebDriver driver
	 * @param locator By locator
	 * @return WebElement element
	 */
	public static WebElement getElement(WebDriver driver, By locator) {
		WebElement element = driver.findElement(locator);
		return element;
	}

	/**
	 * Converts web element locator to By locator
	 * @param webEle WebElement locator
	 * @return By locator
	 */
	public static By webElementToBy(WebElement webEle) {
		try {
			String webEleString = webEle.toString();
			if (webEleString.contains("unknown locator")) {

				Object proxyOrigin = FieldUtils.readField(webEle, "h", true);
				Object loc = FieldUtils.readField(proxyOrigin, "locator", true);

				Object findBy = FieldUtils.readField(loc, "by", true);

				String flag = findBy.toString();
				String[] data = null;
				data = flag.split("\\(");

				String locator = data[0];
				String term = data[1].replace(")", "");
				switch (locator) {
				case "model":
					return ByAngular.model(term);
				case "searchText":
					return ByAngular.buttonText(term);
				case "binding":
					return ByAngular.binding(term);
				case "repeater":
					return ByAngular.repeater(term);
				case "exactBinding":
					return ByAngular.exactBinding(term);
				case "partialButtonText":
					return ByAngular.partialButtonText(term);
				case "options":
					return ByAngular.options(term);
				}

			} else {
				String flag = webEle.toString();
				flag = flag.substring(1, flag.length() - 1);
				String[] data = null;
				if (flag.contains("DefaultElementLocator"))
					data = flag.split("By.")[1].split(": ");
				else
					data = flag.split(" -> ")[1].split(": ");

				String locator = data[0];
				String term = data[1];

				switch (locator) {
				case "xpath":
					return By.xpath(term);
				case "css selector":
					return By.cssSelector(term);
				case "id":
					return By.id(term);
				case "tag name":
					return By.tagName(term);
				case "name":
					return By.name(term);
				case "link text":
					return By.linkText(term);
				case "class name":
					return By.className(term);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error  while converting by type to webelement" + e);
		}
		return (By) webEle;
	}

	@SuppressWarnings("unused")
	public static void initHeal(Exception e, WebElement locator) {
		Boolean value = true;
		System.out.println("Initiating self heal");
		LOGGER.info("Object healing in progress...");
		HealdLctr = smhealer.initiateHealing(e, locator, BrowserDriver.getlthDriver());
		if (HealdLctr == null || smhealer.isShflag() == false) {
			Assert.fail("Object healing  unsuccesful,please refer self healing report.");
		}
		LOGGER.info("Object healing completed. Healed locator : " + HealdLctr);
	}

	public static WebElement getCurrentElement(By currentElement) {
		return BrowserDriver.getDriver().findElement(currentElement);
	}
	
	/**
	 * Enter text in the field
	 * @param locator Locator of the element
	 * @param value Text to be entered
	 */
	public static void enterText(WebElement locator, String value) {
		try {
			locator.clear();
			locator.sendKeys(value);
			LOGGER.info("Text entered successfully" + locator);
			Boolean elemState = CollectElementDetails.elementPresenceCheck(locator);
			if (smhealer.isShflag() && elemState) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				enterText(HealdLctr, value);
			} else {
				LOGGER.error("Error while enterting the text for the locator: " + locator + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while enterting the text for the locator :" + locator);
				Assert.fail("Error while entering the text for the locator: " + locator + "Exception :" + e);
			}
		}
	}

	/**
	 * Clicks a given element 
	 * @param locator Element locator
	 */
	public static void click(WebElement locator) {
		try {
			Boolean elemState = CollectElementDetails.elementPresenceCheck(locator);
			if (smhealer.isShflag() && elemState) {
				CollectElementDetails.elementDetails(locator);
			}
			locator.click();
			LOGGER.info("Click action completed successfully for the locator: " + locator);
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				click(HealdLctr);
			} else {
				LOGGER.error("Error while performing the click action for the locator: " + locator + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing the click action for the locator :" + locator);
				Assert.fail("Error while performing the click action for the locator: " + locator + "Exception :" + e);
			}

		}
	}

	/**
	 * Clicks a given element using JavaScript executor 
	 * @param locator Element locator
	 */
	public static void clickByJsExecutor(WebElement locator) {
		try {
			Boolean elemState = CollectElementDetails.elementPresenceCheck(locator);
			if (smhealer.isShflag() && elemState) {
				CollectElementDetails.elementDetails(locator);
			}
			JavascriptExecutor executor = (JavascriptExecutor) BrowserDriver.getDriver();
			executor.executeScript("arguments[0].click();", locator);
		} catch (Exception e) {
			e.printStackTrace();
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				clickByJsExecutor(HealdLctr);
			} else {
				LOGGER.error("Error while performing the click action for the locator: " + locator + "Exception :" + e);
				String err = BrowserDriver
						.setScrnshoterrmsg("Error while performing the click action for the locator :" + locator);
				JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.getDriver();
				js.executeScript(err);
				Assert.fail("Error while performing the click action for the locator: " + locator + "Exception :" + e);
			}
		}
	}

	public static void scrollBy(int a, int b) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.getDriver();
			js.executeScript("window.scrollBy(" + a + "," + b + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void scrolltoElement(WebElement locator) {
		try {
			((JavascriptExecutor) BrowserDriver.getDriver()).executeScript("arguments[0].scrollIntoView();", locator);
			Boolean elemState = CollectElementDetails.elementPresenceCheck(locator);
			if (smhealer.isShflag() && elemState) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				scrolltoElement(HealdLctr);
			} else {
				LOGGER.error("Error while performing the click action for the locator: " + locator + "Exception :" + e);
				String err = BrowserDriver
						.setScrnshoterrmsg("Error while performing the click action for the locator :" + locator);
				JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.getDriver();
				js.executeScript(err);
				Assert.fail("Error while performing the click action for the locator: " + locator + "Exception :" + e);
			}
		}
	}

	/**
	 * Verify the element text with the given text
	 * 
	 * @param locator  Locator of the element
	 * @param strVText Text to be compared
	 * @return true if the text matches with the element text, else false
	 */
	public static boolean verifyText(WebElement locator, String strVText) {
		boolean vflag = true;
		String actualText = "";
		try {
			if (locator.getTagName().equals("select")) {
				Select seleObj = new Select(locator);
				actualText = seleObj.getFirstSelectedOption().getText().trim();
				vflag = compareText(actualText, strVText);
			} else {
				WebWaitHelper.waitForElementPresence(locator);
				actualText = locator.getText();
				if (actualText == null || actualText.isEmpty()) {
					actualText = locator.getAttribute("innerText");
					if (actualText == null || actualText.isEmpty()) {
						actualText = locator.getAttribute("value");
						vflag = compareText(actualText, strVText);
					} else {
						vflag = compareText(actualText, strVText);
					}

				} else {
					vflag = compareText(actualText, strVText);
				}
			}
			LOGGER.info("Text verified successfully for the locator: " + locator);
			Boolean elemState = CollectElementDetails.elementPresenceCheck(locator);
			if (smhealer.isShflag() && elemState) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				verifyText(HealdLctr, strVText);
			} else {
				LOGGER.error("Error while verifying the text for the locator: " + locator + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while verifying the text for the locator:" + locator);
			}

		}
		return vflag;
	}
	/**
	 * Compare two texts
	 * @param strActualText first text
	 * @param strCompText second text
	 * @return true if the first text matches with the second text, else false
	 */
	public static boolean compareText(String strActualText, String strCompText) {
		boolean compFlag = false;
		try {
			if (strActualText.equals(strCompText)) {
				compFlag = true;
			} else {
				compFlag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error occurred while doing the compareText action " + "Exception :" + e);
		}
		return compFlag;
	}

	public static boolean existText(WebElement locator) {
		boolean flag = true;
		try {
			WebWaitHelper.waitForElement(locator);
			if (locator.getAttribute("value").isEmpty()) {
				flag = false;
				Assert.fail("Text does not exists");
			} else {
				flag = true;
				LOGGER.info("Text exits " + locator);
			}
			Boolean elemState = CollectElementDetails.elementPresenceCheck(locator);
			if (smhealer.isShflag() && elemState) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				existText(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error occurred while trying to check whether the text exists " + "Exception :" + e);
				String err = BrowserDriver
						.setScrnshoterrmsg("Error while trying to check whether the text exists:" + locator);
				JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.getDriver();
				js.executeScript(err);
				Assert.fail("Error occurred while trying to check whether the text exists " + "Exception :" + e);
			}
		}
		return flag;
	}

	public static void clearText(WebElement locator) {
		try {
			By byEle = WebHandlers.webElementToBy(locator);
			WebWaitHelper.waitelementforHealCheck(byEle, 15);
			locator.clear();
			LOGGER.info("Text cleared successfully ");
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				clearText(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error occurred while trying to clear text " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error occurred while trying to clear text :" + locator);
				Assert.fail("Error occurred while trying to clear text " + "Exception :" + e);
			}

		}
	}

	/**
	 * Verify whether the given element is disabled 
	 * @param locator Locator of the element
	 * @return true if the element is disabled, else false
	 */
	public static boolean elementDisabled(WebElement locator) {
		boolean eFlag = false;
		try {
			if (!locator.isEnabled()) {
				eFlag = true;
				LOGGER.info("Element is disabled");
			} else {
				eFlag = false;
				LOGGER.info("Element is enabled");
			}
			LOGGER.info("elementDisabled check completed successfully ");
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				elementDisabled(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error occurred while checking whether the element is disabled " + "Exception :" + e);
				BrowserDriver
						.setScrnshoterrmsg("Error occurred while checking whether the element is disabled :" + locator);
				Assert.fail("Error occurred while checking whether the element is disabled " + "Exception :" + e);
			}
		}
		return eFlag;
	}

	public static String fetchPropertyVal(WebElement locator, String property) {
		String propValue = "";
		try {
			propValue = locator.getAttribute(property).toString();
			LOGGER.info(
					"Property value fetched  successfully for the locator: " + locator + "prop value :" + propValue);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return propValue;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				fetchPropertyVal(HealdLctr, property);
			} else {
				e.printStackTrace();
				LOGGER.error("Error  while fetching  the property value " + "Exception :" + e);
				String err = BrowserDriver.setScrnshoterrmsg("Error while fetching  the property value :" + locator);
				JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.getDriver();
				js.executeScript(err);
				Assert.fail("Error  while fetching  the property value " + "Exception :" + e);
			}
		}
		return propValue;
	}

	public static void verifyProperty(WebElement locator, String property, String expected) {
		String actual = "";
		try {
			actual = fetchPropertyVal(locator, property);
			LOGGER.info("Property value fetched successfully for the locator: " + locator);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				verifyProperty(HealdLctr, property, expected);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying to verify the property " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while trying to verify the property:" + locator);
				Assert.fail("Error while trying to verify the property " + "Exception :" + e);
			}
		}
		if (!expected.equals(actual)) {
			LOGGER.info("property does not  matches" + "locator value: " + locator + " Expected value :" + expected
					+ "Actual value :" + actual);
			Assert.fail("Property does not matches");
		} else {
			LOGGER.info("Property matches" + "locator value: " + locator + " Expected value :" + expected
					+ "Actual value :" + actual);
		}
	}

	public static void doubleClick(WebElement locator) {
		try {
			Boolean elemState = CollectElementDetails.elementPresenceCheck(locator);
			if (smhealer.isShflag() && elemState) {
				CollectElementDetails.elementDetails(locator);
			}
			new Actions(BrowserDriver.getDriver()).doubleClick(locator).perform();
			LOGGER.info("Double click performed successfully");
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				doubleClick(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing double click " + "Exception: " + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing double click:" + locator);
				Assert.fail("Error while performing double click " + "Exception: " + e);
			}
		}

	}

	@SuppressWarnings("unused")
	public static void rightClick(WebElement locator) {
		try {
			new Actions(BrowserDriver.getDriver()).contextClick(locator).perform();
			LOGGER.info("Right click performed successfully");
			Boolean elemState = CollectElementDetails.elementPresenceCheck(locator);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				rightClick(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing right click " + "Exception: " + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing right click:" + locator);
				Assert.fail("Error while performing right click " + "Exception: " + e);
			}
		}
	}

	/**
	 * Verify whether the given element exists
	 * 
	 * @param locator Locator of the element
	 * @return true if the element exists, else false
	 */
	public static boolean elementExists(WebElement locator) {
		boolean flag = false;
		try {
			WebDriverWait wait = new WebDriverWait(BrowserDriver.getDriver(), Duration.ofSeconds(10));
			wait.until(ExpectedConditions.presenceOfElementLocated(WebHandlers.webElementToBy(locator)));
			flag = true;
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			LOGGER.info("elementExists check completed successfully, Element exists ");
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				elementExists(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.info("Element not found. " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Element not found:" + locator);
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * Verify whether the given checkbox is checked
	 * 
	 * @param locator Locator of the checkbox element
	 * @return true if the checkbox is checked, else false
	 */
	public static boolean chkboxIsChecked(WebElement locator) {
		By byEle = WebHandlers.webElementToBy(locator);
		WebWaitHelper.waitelementforHealCheck(byEle, 15);
		boolean eFlag = false;
		try {
			if (locator.isSelected()) {
				eFlag = true;
			} else {
				eFlag = false;
				Assert.fail("Checkbox is not checked");
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				chkboxIsChecked(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the chkboxIsChecked action for the locator: " + locator
						+ "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg(
						"Error while performing the chkboxIsChecked action for the locator :" + locator);
				Assert.fail("Error while performing the chkboxIsChecked action for the locator: " + locator
						+ "Exception :" + e);
			}
		}
		return eFlag;

	}

	public static boolean radiobtnNotChecked(WebElement locator) {
		By byEle = WebHandlers.webElementToBy(locator);
		WebWaitHelper.waitelementforHealCheck(byEle, 15);
		boolean eFlag = false;
		try {
			if (!locator.isSelected()) {
				eFlag = true;
			} else {
				eFlag = false;
				Assert.fail("Radio button is checked");
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				radiobtnNotChecked(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing radiobtnNotChecked action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing radiobtnNotChecked action:" + locator);
				Assert.fail("Error while performing radiobtnNotChecked action " + "Exception :" + e);
			}
		}
		return eFlag;

	}

	public static boolean radioBtnIsSelected(WebElement locator) {
		By byEle = WebHandlers.webElementToBy(locator);
		WebWaitHelper.waitelementforHealCheck(byEle, 15);
		boolean eFlag = false;
		try {
			if (locator.isSelected()) {
				eFlag = true;
			} else {
				eFlag = false;
				Assert.fail("Radio button is not selected");
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				radioBtnIsSelected(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing radioBtnIsSelected action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing radioBtnIsSelected action:" + locator);
				Assert.fail("Error while performing radioBtnIsSelected action " + "Exception :" + e);
			}
		}
		return eFlag;

	}

	public static boolean radioBtnIsNotSelected(WebElement locator) {
		By byEle = WebHandlers.webElementToBy(locator);
		WebWaitHelper.waitelementforHealCheck(byEle, 15);
		boolean eFlag = false;
		try {
			if (!locator.isSelected()) {
				eFlag = true;
			} else {
				eFlag = false;
				Assert.fail("Radio button is  selected");
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				radioBtnIsNotSelected(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing radioBtnIsNotSelected action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing radioBtnIsNotSelected action:" + locator);
				Assert.fail("Error while performing radioBtnIsNotSelected action " + "Exception :" + e);
			}
		}
		return eFlag;

	}

	public static HashMap<Integer, String> sltCtrlReadAllVal(WebElement locator) {
		HashMap<Integer, String> comboValuesMap = new HashMap<Integer, String>();
		try {
			WebWaitHelper.waitForElement(locator);
			comboValuesMap = new HashMap<Integer, String>();
			Select dropdown = new Select(locator);
			List<WebElement> dd = dropdown.getOptions();
			for (int j = 0; j < dd.size(); j++) {
				comboValuesMap.put(j, dd.get(j).getText());
			}
			LOGGER.info("The values are " + comboValuesMap);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return comboValuesMap;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				sltCtrlReadAllVal(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the sltCtrlReadAllVal action for the locator: " + locator
						+ "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg(
						"Error while performing the sltCtrlReadAllVal action for the locator:" + locator);
				Assert.fail("Error while performing the sltCtrlReadAllVal action for the locator: " + locator
						+ "Exception :" + e);
			}
		}
		return comboValuesMap;
	}

	public static String dropDownGetCurrentSelection(WebElement locator) {
		String cmbSelectedValue = "";
		try {
			WebWaitHelper.waitForElement(locator);
			Select seleObj = new Select(locator);
			cmbSelectedValue = seleObj.getFirstSelectedOption().getText().trim();
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				dropDownGetCurrentSelection(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying to get current selection of dropdown for the locator: " + locator
						+ "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg(
						"Error while trying to get current selection of dropdown for the locator:" + locator);
				Assert.fail("Error while trying to get current selection of dropdown for the locator " + locator
						+ "Exception :" + e);
			}
		}
		return cmbSelectedValue;

	}

	public static void dropDownSelectByIndex(WebElement locator, int index) {
		try {
			WebWaitHelper.waitForElement(locator);
			String cmbSelectedValue = "";
			Select dropdown = new Select(locator);
			List<WebElement> cmbOptions = dropdown.getOptions();
			cmbSelectedValue = cmbOptions.get(index).getText();
			LOGGER.info("Selected value :" + cmbSelectedValue);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				dropDownSelectByIndex(HealdLctr, index);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying to select index from drop down  the locator: " + locator
						+ "Exception :" + e);
				BrowserDriver
						.setScrnshoterrmsg("Error while trying to select index from drop down  the locator:" + locator);
				Assert.fail("Error while trying to select index from drop down  the locator: " + locator + "Exception :"
						+ e);

			}
		}
	}

	public static boolean dropDownSetByVal(WebElement locator, String value) {
		boolean flag = false;
		try {
			WebWaitHelper.waitForElement(locator);
			Select dropdown = new Select(locator);
			List<WebElement> cmbOptions = dropdown.getOptions();
			for (int i = 0; i <= cmbOptions.size(); i++) {
				if (value.equalsIgnoreCase(cmbOptions.get(i).getText())) {
					dropdown.selectByIndex(i);
					flag = true;
					break;
				}
			}
			LOGGER.info(" The action dropDownSetByVal completed successfully ");
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return flag;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				dropDownSetByVal(HealdLctr, value);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying to set  drop down by value for the locator: " + locator + "Exception :"
						+ e);
				BrowserDriver
						.setScrnshoterrmsg("Error while trying to set  drop down by value for the locator:" + locator);
				Assert.fail(
						"Error while trying to set drop down by value for the locator:" + locator + "Exception :" + e);
			}
		}
		return flag;
	}

	public static boolean dropDownSetByIndex(WebElement locator, int index) {
		boolean flag = false;
		try {
			WebWaitHelper.waitForElement(locator);
			Select dropdown = new Select(locator);
			List<WebElement> cmbOptions = dropdown.getOptions();
			for (int i = 0; i <= cmbOptions.size(); i++) {
				if (!cmbOptions.get(i).getText().isEmpty()) {
					dropdown.selectByIndex(index);
					flag = true;
					break;
				}
			}
			LOGGER.info(" The action dropDownSetByIndex completed successfully ");
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return flag;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				dropDownSetByIndex(HealdLctr, index);
			} else {
				e.printStackTrace();
				LOGGER.error(
						"Error while trying to set drop down by index for the locator: " + locator + "Exception :" + e);
				BrowserDriver
						.setScrnshoterrmsg("Error while trying to set drop down by index for the locator: " + locator);
				Assert.fail(
						"Error while trying to set drop down by index for the locator:" + locator + "Exception :" + e);
			}
		}
		return flag;
	}

	// Code added to handle windows
	/**
	 * Gets the window handles(opened tabs)
	 * 
	 * @return String array containing window handles
	 */
	public static String[] getWindowHandles() {
		String[] handles = new String[BrowserDriver.getDriver().getWindowHandles().size()];
		try {
			BrowserDriver.getDriver().getWindowHandles().toArray(handles);
		} catch (Exception e) {
			LOGGER.error("Error occurred while getting window handles" + "Exception" + e);
		}

		return handles;
	}

	/**
	 * Switches to parent window
	 */
	public static void switchToDefaultWindow() {
		try {
			getWindowHandles();
			BrowserDriver.getDriver().switchTo().window(getWindowHandles()[0]);
			LOGGER.info("switched back to default window successfully");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error occurred while trying to switch back to default window" + "Exception :" + e);
			Assert.fail("Error occurred while trying to switch back to default window" + "Exception :" + e);
		}
	}

	/**
	 * Switches to newly opened window
	 */
	public static void switchToNewWindow() {
		try {
			String[] handles = getWindowHandles();
			int i = handles.length - 1;
			BrowserDriver.getDriver().switchTo().window(getWindowHandles()[i]);
			LOGGER.info("switched to new window successfully");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error while trying to switch to the new window " + "Exception :" + e);
			Assert.fail("Error while trying to switch to the new window " + "Exception :" + e);
		}
	}

	/**
	 * Switches to a frame
	 * @param locator Locator of the frame
	 */
	public static void switchToFrame(WebElement locator) throws Exception {
		try {
			// actual = String.valueOf(locator.isDisplayed());
			BrowserDriver.getDriver().switchTo().frame(locator);
			InsideFrame = true;
			/*
			 * if (actual == null) throw new WebAutomationException(locator,
			 * "Switch to Frame", "Frame not Found"); actual = actual.trim().toLowerCase();
			 */

			List<WebElement> iframes = BrowserDriver.getDriver().findElements(By.tagName("iframe"));

			for (int i = 0; i < iframes.size(); i++) {
				System.out.println(iframes.get(i));
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}

		} catch (WebAutomationException e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				switchToFrame(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Tried switching to next frame, not found " + locator + "Exception: " + e);
				BrowserDriver.setScrnshoterrmsg("Tried switching to next frame, not found: " + locator);
				Assert.fail("Tried switching to next frame, not found " + locator + "Exception: " + e);
				// throw new WebAutomationException(locator, "Switch to Frame",
				// "Frame not Found");
			}
		}
	}

	public static void switchBackFromFrame() {
		try {
			BrowserDriver.getDriver().switchTo().defaultContent();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error while trying to perform switch back from frame" + " Exception: " + e);
			Assert.fail("Error while trying to perform switch back from frame" + " Exception: " + e);
		}
	}

	// Code added to handle alerts
	/**
	 * Switch to alert 
	 * @return Alert which is popped up
	 */
	public static Alert switchToAlert() {
		Alert alert = BrowserDriver.getDriver().switchTo().alert();
		return alert;
	}

	/**
	 * Accept an alert
	 */
	public static void acceptAlert() {
		try {
			WebDriverWait wait = new WebDriverWait(BrowserDriver.getDriver(), Duration.ofSeconds(10));
			wait.until(ExpectedConditions.alertIsPresent());
			switchToAlert().accept();
			LOGGER.info("Alert accepted");
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, null);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying accept alert " + " Exception: " + e);
				Assert.fail("Error while trying to accept alert " + " Exception: " + e);
			}
		}
	}

	/**
	 * Dismiss an alert
	 */
	public static void dismissAlert() {
		try {
			WebDriverWait wait = new WebDriverWait(BrowserDriver.getDriver(), Duration.ofSeconds(10));
			wait.until(ExpectedConditions.alertIsPresent());
			switchToAlert().dismiss();
			LOGGER.info("dismissAlert action completed successfully");
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, null);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying to dismiss alert " + " Exception: " + e);
				Assert.fail("Error while trying to dismiss alert " + " Exception: " + e);
			}
		}

	}

	public static boolean multiDeselectByText(WebElement locator, String options) {
		boolean flag = false;
		ArrayList<String> optionList = null;
		try {
			WebWaitHelper.waitForElement(locator);
			optionList = optionsSplit(options);
			if (optionList != null) {
				for (int i = 0; i < optionList.size(); i++) {
					Select multiple = new Select(locator);
					multiple.deselectByVisibleText(optionList.get(i));
				}
				flag = true;
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return flag;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				multiDeselectByText(HealdLctr, options);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying to perform multideselect by text " + " Exception: " + e);
				BrowserDriver.setScrnshoterrmsg("Error while trying to perform multideselect: " + locator);
				Assert.fail("Error while trying to perform multideselect by text " + " Exception: " + e);
			}
		}
		return flag;
	}

	public static boolean multiDeselectAll(WebElement locator) {
		boolean flag = false;
		try {
			WebWaitHelper.waitForElement(locator);
			Select multiple = new Select(locator);
			multiple.deselectAll();
			flag = true;
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return flag;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				multiDeselectAll(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying to perform multideselect" + " Exception: " + e);
				BrowserDriver.setScrnshoterrmsg("Error while trying to perform multideselect: " + locator);
				Assert.fail("Error while trying to perform multideselect" + " Exception: " + e);
			}
		}
		return flag;

	}

	public static boolean multiSelectByText(WebElement locator, String options) {
		boolean flag = false;
		ArrayList<String> optionList = null;
		try {
			WebWaitHelper.waitForElement(locator);
			optionList = optionsSplit(options);
			if (optionList != null) {
				for (int i = 0; i < optionList.size(); i++) {
					Select multiple = new Select(locator);
					multiple.selectByVisibleText(optionList.get(i));
				}

				flag = true;
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return flag;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				multiSelectByText(HealdLctr, options);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while trying to perform multiSelectByText" + " Exception: " + e);
				BrowserDriver.setScrnshoterrmsg("Error while trying to perform multiSelectByText: " + locator);
				Assert.fail("Error while trying to perform multiSelectByText" + " Exception: " + e);
			}
		}
		return flag;

	}

	public static boolean multiSelectByIndex(WebElement locator, String indexes) {
		boolean flag = false;
		ArrayList<String> indexList = null;
		try {
			WebWaitHelper.waitForElement(locator);
			indexList = optionsSplit(indexes);
			if (indexList != null) {
				ArrayList<Integer> intList = (ArrayList<Integer>) indexList.stream().map(Integer::parseInt)
						.collect(Collectors.toList());
				for (int i = 0; i < intList.size(); i++) {
					Select multiple = new Select(locator);
					multiple.selectByIndex(intList.get(i));
				}
			}

			flag = true;
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return flag;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				multiSelectByText(HealdLctr, indexes);
			} else {
				e.printStackTrace();
				LOGGER.error(
						"Error while trying to perform multiSelect by providing an index value" + " Exception: " + e);
				BrowserDriver.setScrnshoterrmsg(
						"Error while trying to perform multiSelect by providing an index value: " + locator);
				Assert.fail(
						"Error while trying to perform multiSelect by providing an index value" + " Exception: " + e);
			}
		}
		return flag;
	}
	
	/**
	 * Get the header map of a table 
	 * @param locator Table element locator
	 * @return LinkedHashMap containing value and index of each headers
	 */
	public static LinkedHashMap<String, Integer> getTblHeaderVal(WebElement locator) {
		LinkedHashMap<String, Integer> headermap = new LinkedHashMap<String, Integer>();
		try {
			WebWaitHelper.waitForElement(locator);
			WebElement mytableHead = locator.findElement(By.tagName("thead"));
			List<WebElement> rowsTable = mytableHead.findElements(By.tagName("tr"));
			for (int row = 0; row < rowsTable.size(); row++) {
				List<WebElement> colRow = rowsTable.get(row).findElements(By.tagName("th"));
				for (int column = 0; column < colRow.size(); column++) {
					headermap.put(colRow.get(column).getText(), column);
				}
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			LOGGER.info("getTblHeaderVal action completed successfully");
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getTblHeaderVal(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error  while performing getTblHeaderVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error  while performing getTblHeaderVal action: " + locator);
				Assert.fail("Error  while performing getTblHeaderVal action " + "Exception :" + e);
			}
		}
		return headermap;
	}
	
	/**
	 * Get the body map of a table 
	 * @param locator Table element locator
	 * @return LinkedHashMap containing value and index of each body element
	 */
	public static LinkedHashMap<String, Integer> getTblBodyVal(WebElement locator) {
		LinkedHashMap<String, Integer> bodymap = new LinkedHashMap<String, Integer>();
		try {
			WebWaitHelper.waitForElement(locator);
			WebElement mytableBody = locator.findElement(By.tagName("tbody"));
			List<WebElement> rowsTable = mytableBody.findElements(By.tagName("tr"));
			for (int row = 0; row < rowsTable.size(); row++) {
				List<WebElement> colRow = rowsTable.get(row).findElements(By.tagName("td"));
				for (int column = 0; column < colRow.size(); column++) {
					bodymap.put(colRow.get(column).getText(), column);
				}
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			LOGGER.info("getTblBodyVal action completed successfully");
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getTblBodyVal(HealdLctr);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the getTblBodyVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing the getTblBodyVal action: " + locator);
				Assert.fail("Error while performing the getTblBodyVal action " + "Exception :" + e);
			}
		}
		return bodymap;
	}

	public static ArrayList<String> optionsSplit(String options) {
		ArrayList<String> tempOptionList = null;
		try {
			String[] items = options.split(":");
			tempOptionList = new ArrayList<String>(Arrays.asList(items));
			return tempOptionList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error while performing  action optionsSplit" + " Exception: " + e);
			BrowserDriver.setScrnshoterrmsg("Error while performing  action optionsSplit ");
			Assert.fail("Error while performing  action optionsSplit" + " Exception: " + e);
		}
		return tempOptionList;
	}

	public static String getTblTdVal(WebElement locator, int rowIndex, int colIndex) {
		WebElement ele = null;
		WebElement rowele = null;
		String tblCellValue = "";

		try {
			WebWaitHelper.waitForElement(locator);
			List<WebElement> mytables = locator.findElements(By.tagName("tr"));
			rowele = mytables.get(rowIndex);
			List<WebElement> rowsTable = rowele.findElements(By.tagName("td"));
			ele = rowsTable.get(colIndex);
			tblCellValue = ele.getText();
			LOGGER.info("The getTblTdVal action completed successfully");
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getTblTdVal(HealdLctr, rowIndex, colIndex);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the getTblTdVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing the getTblTdVal action: " + locator);
				Assert.fail("Error  while performing the getTblTdVal action " + "Exception :" + e);
			}
		}
		return tblCellValue;
	}

	public static String getTblThVal(WebElement locator, int rowIndex, int colIndex) {

		WebElement ele = null;
		WebElement tblEle = null;
		// WebElement rowEle = null;

		WebElement myTblHead = null;
		List<WebElement> myTblHeadRow = null;
		List<WebElement> myTblHeadRowCol = null;
		String tblCellValue = "";
		try {
			WebWaitHelper.waitForElement(locator);
			myTblHead = locator.findElement(By.tagName("thead"));
			myTblHeadRow = myTblHead.findElements(By.xpath("tr"));
			for (int row = 0; row < myTblHeadRow.size(); row++) {
				tblEle = myTblHeadRow.get(row);
				myTblHeadRowCol = tblEle.findElements(By.tagName("th"));
				// List<WebElement> hdrRowCol =
				// rowEle.findElements(By.tagName("th"));
				ele = myTblHeadRowCol.get(colIndex);
				tblCellValue = ele.getText();
			}
			LOGGER.info("getTblThVal action completed successfully");
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getTblThVal(HealdLctr, rowIndex, colIndex);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the getTblThVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing the getTblThVal action: " + locator);
				Assert.fail("Error  while performing the getTblThVal action " + "Exception :" + e);
			}
		}
		return tblCellValue;

	}

	/**
	 * Get the index of the value provided starting from 0
	 * 
	 * @param locator Table element locator
	 * @param value   String value of the element to be searched (this will consider
	 *                the first occurrence of the value)
	 * @return String containing index as row,column of the value starting from 0
	 */
	public static String getIndexofVal(WebElement locator, String value) {
		// WebElement ele = null;
		WebElement mytableBody = null;
		List<WebElement> mytableBodyrow = null;
		WebElement tblEle = null;
		List<WebElement> mytableBodycol = null;
		String indexVal = "";
		try {
			WebWaitHelper.waitForElement(locator);
			mytableBody = locator.findElement(By.tagName("tbody"));
			mytableBodyrow = mytableBody.findElements(By.tagName("tr"));
			for (int row = 0; row < mytableBodyrow.size(); row++) {
				tblEle = mytableBodyrow.get(row);
				mytableBodycol = tblEle.findElements(By.tagName("td"));
				for (int column = 0; column < mytableBodycol.size(); column++) {
					if (mytableBodycol.get(column).getText().equalsIgnoreCase(value)) {
						indexVal = row + "," + column;
						break;
					}
				}
			}
			LOGGER.info("getIndexofVal action completed successfully");
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getIndexofVal(HealdLctr, value);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the getIndexofVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing the getIndexofVal action: " + locator);
				Assert.fail("Error while performing the getIndexofVal action " + "Exception :" + e);
			}
		}
		return indexVal;

	}

	/**
	 * Get the index of the value provided starting from 1
	 * 
	 * @param locator Table element locator
	 * @param value   String value of the element to be searched (this will consider
	 *                the first occurrence of the value)
	 * @return String containing index as row,column of the value starting from 1
	 */
	public static String getFirstIndexofVal(WebElement locator, String value) {
		WebElement mytableBody = null;
		List<WebElement> mytableBodyrow = null;
		WebElement tblEle = null;
		List<WebElement> mytableBodycol = null;
		String indexVal = "";
		boolean flag = false;
		try {
			WebWaitHelper.waitForElementPresence(locator);
			mytableBody = locator.findElement(By.tagName("tbody"));
			mytableBodyrow = mytableBody.findElements(By.tagName("tr"));
			for (int row = 0; row < mytableBodyrow.size(); row++) {
				tblEle = mytableBodyrow.get(row);
				mytableBodycol = tblEle.findElements(By.tagName("td"));
				for (int column = 0; column < mytableBodycol.size(); column++) {
					if (mytableBodycol.get(column).getText().equalsIgnoreCase(value)) {
						row = row + 1;
						column = column + 1;
						LOGGER.info("Index of " + value + " is (" + row + "," + column + ")");
						indexVal = row + "," + column;
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getFirstIndexofVal(HealdLctr, value);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the getFirstIndexofVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing the getFirstIndexofVal action: " + locator);
				Assert.fail("Error while performing the getFirstIndexofVal action " + "Exception :" + e);
			}
		}
		return indexVal;

	}
	/**
	 * Get the column map of a table by providing the column header value
	 * 
	 * @param locator Table element locator
	 * @param colHeader   String value of the header to be searched
	 * @return LinkedHashMap containing index and value of each row of the column
	 */
	public static LinkedHashMap<String, String> getColMapByHdrVal(WebElement locator, String colHeader) {
		LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
		LinkedHashMap<String, Integer> headerMap = new LinkedHashMap<String, Integer>();
		// WebElement ele = null;
		WebElement mytableBody = null;
		List<WebElement> mytableBodyrow = null;
		WebElement tblEle = null;
		List<WebElement> mytableBodycol = null;
		try {
			WebWaitHelper.waitForElement(locator);
			headerMap = getTblHeaderVal(locator);
			int colNum = headerMap.get(colHeader);
			mytableBody = locator.findElement(By.tagName("tbody"));
			mytableBodyrow = mytableBody.findElements(By.tagName("tr"));
			for (int row = 0; row < mytableBodyrow.size(); row++) {
				tblEle = mytableBodyrow.get(row);
				mytableBodycol = tblEle.findElements(By.tagName("td"));
				colMap.put(String.valueOf(row),mytableBodycol.get(colNum).getText());
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return colMap;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getColMapByHdrVal(HealdLctr, colHeader);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the getColMapByHdrVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing the getColMapByHdrVal action: " + locator);
				Assert.fail("Error while performing the getColMapByHdrVal action " + "Exception :" + e);
			}
		}
		return colMap;
	}

	/**
	 * Get the row map of a table by providing the row index
	 * 
	 * @param locator Table element locator
	 * @param rowIndex index of the row
	 * @return LinkedHashMap containing index and value of each column of the row
	 */
	public static LinkedHashMap<String, String> getRowMapByIndxVal(WebElement locator, int rowIndex) {
		LinkedHashMap<String, String> rowMap = new LinkedHashMap<String, String>();
		// HashMap<String, Integer> bodyMap = new HashMap<String, Integer>();
		WebElement mytableBody = null;
		List<WebElement> mytableBodyrow = null;
		try {
			WebWaitHelper.waitForElement(locator);
			mytableBody = locator.findElement(By.tagName("tbody"));
			mytableBodyrow = mytableBody.findElements(By.tagName("tr"));
			for (int i = 0; i < mytableBodyrow.get(rowIndex).findElements(By.tagName("td")).size(); i++) {

				rowMap.put(String.valueOf(i),mytableBodyrow.get(rowIndex).findElements(By.tagName("td")).get(i).getText());
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return rowMap;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getRowMapByIndxVal(HealdLctr, rowIndex);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while perfoming the getRowMapByIndxVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while perfoming the getRowMapByIndxVal action: " + locator);
				Assert.fail("Error while perfoming the getRowMapByIndxVal action " + "Exception :" + e);
			}
		}
		return rowMap;
	}
	
	/**
	 * Get the row map of a table by providing the row header value
	 * 
	 * @param locator Table element locator
	 * @param colHeader String value of the header to be searched
	 * @return LinkedHashMap containing index and value of each column of the row
	 */
	public static LinkedHashMap<String, String> getRowMapByHdrVal(WebElement locator, String rowHeader) {
		LinkedHashMap<String, String> rowMap = new LinkedHashMap<String, String>();
		HashMap<String, Integer> bodyMap = new HashMap<String, Integer>();
		WebElement mytableBody = null;
		List<WebElement> mytableBodyrow = null;
		try {
			WebWaitHelper.waitForElement(locator);
			bodyMap = getTblBodyVal(locator);
			int rowNum = bodyMap.get(rowHeader);
			mytableBody = locator.findElement(By.tagName("tbody"));
			mytableBodyrow = mytableBody.findElements(By.tagName("tr"));
			for (int i = 0; i < mytableBodyrow.get(rowNum).findElements(By.tagName("td")).size(); i++) {

				rowMap.put(String.valueOf(i), mytableBodyrow.get(rowNum).findElements(By.tagName("td")).get(i).getText());
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return rowMap;

		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getRowMapByHdrVal(HealdLctr, rowHeader);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the getRowMapByHdrVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error  while performing the getRowMapByHdrVal action: " + locator);
				Assert.fail("Error  while performing the getRowMapByHdrVal action " + "Exception :" + e);
			}
		}
		return rowMap;
	}
	
	/**
	 * Get the row map of a table by providing the column index
	 * 
	 * @param locator Table element locator
	 * @param rowIndex index of the column
	 * @return LinkedHashMap containing index and value of each row of the column
	 */
	public static LinkedHashMap<String, String> getColMapByIndxVal(WebElement locator, int colIndex) {
		LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
		WebElement mytableBody = null;
		List<WebElement> mytableBodyrow = null;
		WebElement mytableBodyrowCol = null;
		try {
			WebWaitHelper.waitForElement(locator);
			mytableBody = locator.findElement(By.tagName("tbody"));
			mytableBodyrow = mytableBody.findElements(By.tagName("tr"));
			for (int i = 0; i < mytableBodyrow.size(); i++) {
				mytableBodyrowCol = mytableBodyrow.get(i).findElements(By.tagName("td")).get(colIndex);
				colMap.put(String.valueOf(i),mytableBodyrowCol.getText());
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
			return colMap;
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				getColMapByIndxVal(HealdLctr, colIndex);
			} else {
				e.printStackTrace();
				LOGGER.error("Error while performing the getColMapByIndxVal action " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error while performing the getColMapByIndxVal action: " + locator);
				Assert.fail("Error  while performing the getColMapByIndxVal action " + "Exception :" + e);
			}
		}
		return colMap;
	}

	/**
	 * Click any checkbox element in a table by specifying the string value of an
	 * element which shares the same row with the checkbox to be clicked.
	 * 
	 * @param tbllocator  Table element
	 * @param value       String value of an element which is in the required row
	 *                    (this will consider the first occurrence of the value)
	 * @param chkColIndex Column index of the checkbox element to be clicked
	 *                    starting from 1
	 */
	public static void TblCelChkboxClick(WebElement tbllocator, String value, int chkColIndex) {
		WebElement mytableBody = null;
		String[] arrSplit = null;
		try {
			WebWaitHelper.waitForElementPresence(tbllocator);
			String index = getFirstIndexofVal(tbllocator, value);
			arrSplit = index.split(",");

			mytableBody = tbllocator.findElement(By.tagName("tbody"));
			if (mytableBody != null && arrSplit != null) {
				WebElement eleRowCol = mytableBody
						.findElement(By.xpath("tr[" + arrSplit[0] + "]//td[" + chkColIndex + "]//input"));
				eleRowCol.click();
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(tbllocator);
			}
		} catch (Exception e) {
			try {
				if (mytableBody != null && arrSplit != null) {
					WebElement eleRowCol = mytableBody
							.findElement(By.xpath("tr[" + arrSplit[0] + "]//td[" + chkColIndex + "]"));
					eleRowCol.click();
				}
			} catch (Exception e1) {
				e.printStackTrace();
				LOGGER.error("Error  while trying to click a checkbox inside table" + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error  while trying to click a checkbox inside table");
				Assert.fail("Error  while trying to click a checkbox inside table " + "Exception :" + e);
			}
		}
	}

	/**
	 * Click any table element by specifying the string value of an element which
	 * shares the same row with the element to be clicked.
	 * 
	 * @param tbllocator Table element
	 * @param value      String value of an element which is in the required row
	 *                   (this will consider the first occurrence of the value)
	 * @param eleIndex   Column index of the element to be clicked starting from 1
	 * @param tagName    tag name or path of the element to be clicked
	 */
	public static void TblCelEleClick(WebElement tbllocator, String value, int eleIndex, String tagName) { //modified parameter
		WebElement mytableBody = null;
		try {
			String index = getFirstIndexofVal(tbllocator, value);
			String[] arrSplit = index.split(",");

			WebWaitHelper.waitForElement(tbllocator);
			mytableBody = tbllocator.findElement(By.tagName("tbody"));
			WebElement eleRowCol = mytableBody
					.findElement(By.xpath("//tr[" + arrSplit[0] + "]/td[" + eleIndex + "]/" + tagName));
			eleRowCol.click();
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(tbllocator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, tbllocator);
				TblCelEleClick(HealdLctr, value, eleIndex, tagName);
			} else {
				e.printStackTrace();
				LOGGER.error("Error  while trying to click an element inside table" + "Exception :" + e);
				Assert.fail("Error  while trying to click an element inside table " + "Exception :" + e);
			}
		}
	}

	/**
	 * Click any cell link by specifying the table locator and string value of the
	 * link to be clicked
	 * 
	 * @param locator Table element locator
	 * @param value   String value of the link to be clicked
	 */
	public static void TblCelLinkClick(WebElement locator, String value) {
		WebElement mytableBody = null;
		List<WebElement> mytableBodyrow = null;
		try {
			String index = getIndexofVal(locator, value);
			String[] arrSplit = index.split(",");
			WebWaitHelper.waitForElement(locator);
			mytableBody = locator.findElement(By.tagName("tbody"));
			mytableBodyrow = mytableBody.findElements(By.tagName("tr"));
			WebElement eleRow = mytableBodyrow.get(Integer.parseInt(arrSplit[0]));
			WebElement eleRowCol = eleRow.findElements(By.tagName("td")).get(Integer.parseInt(arrSplit[1]));
			WebElement ele = eleRowCol.findElement(By.tagName("a"));
			ele.click();
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {
				initHeal(e, locator);
				TblCelLinkClick(HealdLctr, value);
			} else {
				e.printStackTrace();
				LOGGER.error("Error  while trying to click a link inside table " + "Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Error  while trying to click a link inside table: " + locator);
				Assert.fail("Error  while trying to click a link inside table " + "Exception :" + e);
			}
		}
	}

	public static void uploadFile(String fileNameWithExtention) {
		try {
			String uploadFilePath = FileUtils.getFilePath("src/main/resources/Upload/" + fileNameWithExtention);
			String DriverPath = WebDriverPathUtil.getWiniumDriverPath();
			Process process = Runtime.getRuntime().exec("taskkill /F /IM Winium.Desktop.Driver.exe");
			process.waitFor();
			process.destroy();
			DesktopOptions options = new DesktopOptions();
			options.setApplicationPath("C:/Windows/System32/openfiles.exe");
			WiniumDriverService service = new WiniumDriverService.Builder().usingDriverExecutable(new File(DriverPath))
					.usingPort(9999).withVerbose(true).withSilent(false).buildDesktopService();
			service.start();
			desktopDriver = new WiniumDriver(service, options);
			desktopDriver.findElement(By.name("File name:")).sendKeys(uploadFilePath);
			desktopDriver.findElement(By.xpath("//*[@Name='Cancel']//preceding-sibling::*[@Name='Open']")).click();
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			LOGGER.error("Error while uploading file" + e);
			BrowserDriver.setScrnshoterrmsg("Error while uploading file");
			Assert.fail("Error while uploading file" + e);
		}
	}

	public static void divSpanListBox(WebElement locator, String value) {
		try {
			WebWaitHelper.waitForElement(locator);
			locator.click();
			List<WebElement> listSpan = locator.findElements(By.tagName("span"));
			for (WebElement span : listSpan) {
				if (span.getText().trim().equalsIgnoreCase(value)) {
					span.click();
					break;
				}
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			if (smhealer.isShflag() || smhealer.isImpactflag()) {// check selfhealing logic
				initHeal(e, locator);
				divSpanListBox(HealdLctr, value);
			} else {
				LOGGER.error("Exception :" + e);
				BrowserDriver.setScrnshoterrmsg("Exception: " + locator);
				Assert.fail("Exception :" + e);
			}
		}
	}

	// SFDC Related Web Handler Functions

	public static void divLinkListBox(WebElement locator, String value) {
		try {
			WebWaitHelper.waitForElement(locator);
			locator.click();
			List<WebElement> listSpan = locator.findElements(By.xpath("//a[@role='menuitemradio']"));
			for (WebElement span : listSpan) {
				if (span.getText().trim().equalsIgnoreCase(value)) {
					LOGGER.info("Selected Value is " + span.getText());
					span.click();
					break;
				}
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			LOGGER.error("Exception :" + e);
			String err = BrowserDriver
					.setScrnshoterrmsg("Exception while selecting the value from the list box: " + locator);
			JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.getDriver();
			js.executeScript(err);
			Assert.fail("Exception :" + e);
		}
	}

	public static boolean vfySuccsMsg(WebElement txtSuccess) throws IOException {
		txtSuccess.getText();
		if (txtSuccess.getText().contains("success") || txtSuccess.getText().contains("created")) {
			return true;
		}
		return false;
	}

	public static void divListBox(WebElement locator, String value) {
		try {
			WebWaitHelper.waitForElement(locator);
			locator.click();
			List<WebElement> listSpan = locator.findElements(By.xpath("//a[@role='option']//div//div"));
			for (WebElement span : listSpan) {
				if (span.getText().trim().equalsIgnoreCase(value)) {
					LOGGER.info("Selected Value is " + span.getText());
					span.click();
					break;
				}
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			LOGGER.error("Exception :" + e);
			BrowserDriver.setScrnshoterrmsg("Exception while selecting the value from the list box: " + locator);
			Assert.fail("Exception :" + e);
		}
	}

	public static void TblSFDCThCelLkClk(WebElement tbllocator, String value) {

		WebElement mytableBody = null;
		List<WebElement> mytableBodyrow = null;
		try {
			WebWaitHelper.waitForElement(tbllocator);
			mytableBody = tbllocator.findElement(By.tagName("tbody"));
			mytableBodyrow = mytableBody.findElements(By.tagName("tr"));
			for (WebElement row : mytableBodyrow) {
				WebElement eleRowCol = row.findElement(By.tagName("a"));
				if (eleRowCol.getText().equalsIgnoreCase(value)) {
					LOGGER.info("Selected Value is " + eleRowCol.getText());
					eleRowCol.click();
					break;
				}
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(tbllocator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error  while trying to click an element inside table" + "Exception :" + e);
			BrowserDriver.setScrnshoterrmsg("Exception while trying to click an element inside table: " + tbllocator);
			Assert.fail("Error  while trying to click an element inside table " + "Exception :" + e);
		}

	}

	public static void divSFDCLnkLstBx(WebElement locator, String value) {
		try {
			WebWaitHelper.waitForElement(locator);
			locator.click();
			List<WebElement> listSpan = locator.findElements(By.xpath("//a[@role='menuitemradio']"));
			for (WebElement span : listSpan) {
				if (span.getText().trim().equalsIgnoreCase(value)) {
					LOGGER.info("Selected Value is " + span.getText());
					span.click();
					break;
				}
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			LOGGER.error("Exception :" + e);
			BrowserDriver.setScrnshoterrmsg("Exception while trying to click an element inside list box: " + locator);
			Assert.fail("Exception :" + e);
		}
	}
//end of SFDC
	
	/**
	 * Enter text using JavaScript executor
	 * @param locator Locator of the element
	 * @param text    Text to be entered
	 */
	public static void enterTxtByJavaScript(WebElement locator, String text) throws IOException {
		try {
			JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.getDriver();
			js.executeScript("arguments[0].value = '" + text + "';", locator);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			LOGGER.error("Exception :" + e);
			BrowserDriver.setScrnshoterrmsg("Exception while entering value to a textbox using javascript: " + locator);
			Assert.fail("Exception :" + e);
		}
	}

	public static void enterKey(WebElement locator, String keyName) {
		try {

			keyName = keyName.toLowerCase();
			switch (keyName) {

			case "arrowdown":
				locator.sendKeys(Keys.ARROW_DOWN);
				LOGGER.info("Key entered successfully" + keyName);

			case "arrowup":
				locator.sendKeys(Keys.ARROW_UP);
				LOGGER.info("Key entered successfully" + keyName);

			case "arrowleft":
				locator.sendKeys(Keys.ARROW_LEFT);
				LOGGER.info("Key entered successfully" + keyName);

			case "arrowright":
				locator.sendKeys(Keys.ARROW_RIGHT);
				LOGGER.info("Key entered successfully" + keyName);

			case "backspace":
				locator.sendKeys(Keys.BACK_SPACE);
				LOGGER.info("Key entered successfully" + keyName);

			case "ctrl":
				locator.sendKeys(Keys.CONTROL);
				LOGGER.info("Key entered successfully" + keyName);

			case "alt":
				locator.sendKeys(Keys.ALT);
				LOGGER.info("Key entered successfully" + keyName);

			case "delete":
				locator.sendKeys(Keys.DELETE);
				LOGGER.info("Key entered successfully" + keyName);

			case "enter":
				locator.sendKeys(Keys.ENTER);
				LOGGER.info("Key entered successfully" + keyName);

			case "shift":
				locator.sendKeys(Keys.SHIFT);
				LOGGER.info("Key entered successfully" + keyName);

			case "spacebar":
				locator.sendKeys(Keys.SPACE);
				LOGGER.info("Key entered successfully" + keyName);

			case "tab":
				locator.sendKeys(Keys.TAB);
				LOGGER.info("Key entered successfully" + keyName);

			case "equals":
				locator.sendKeys(Keys.EQUALS);
				LOGGER.info("Key entered successfully" + keyName);

			case "esc":
				locator.sendKeys(Keys.ESCAPE);
				LOGGER.info("Key entered successfully" + keyName);

			case "home":
				locator.sendKeys(Keys.HOME);
				LOGGER.info("Key entered successfully" + keyName);

			case "end":
				locator.sendKeys(Keys.END);
				LOGGER.info("Key entered successfully" + keyName);

			case "insert":
				locator.sendKeys(Keys.INSERT);
				LOGGER.info("Key entered successfully" + keyName);

			case "pageup":
				locator.sendKeys(Keys.PAGE_UP);
				LOGGER.info("Key entered successfully" + keyName);

			case "pagedown":
				locator.sendKeys(Keys.PAGE_DOWN);
				LOGGER.info("Key entered successfully" + keyName);

			case "f1":
				locator.sendKeys(Keys.F1);
				LOGGER.info("Key entered successfully" + keyName);

			case "f2":
				locator.sendKeys(Keys.F2);
				LOGGER.info("Key entered successfully" + keyName);

			case "F3":
				locator.sendKeys(Keys.F3);
				LOGGER.info("Key entered successfully" + keyName);

			case "f4":
				locator.sendKeys(Keys.F4);
				LOGGER.info("Key entered successfully" + keyName);

			case "f5":
				locator.sendKeys(Keys.F5);
				LOGGER.info("Key entered successfully" + keyName);

			case "f6":
				locator.sendKeys(Keys.F6);
				LOGGER.info("Key entered successfully" + keyName);

			case "f7":
				locator.sendKeys(Keys.F7);
				LOGGER.info("Key entered successfully" + keyName);

			case "f8":
				locator.sendKeys(Keys.F8);
				LOGGER.info("Key entered successfully" + keyName);

			case "f9":
				locator.sendKeys(Keys.F9);
				LOGGER.info("Key entered successfully" + keyName);

			case "f10":
				locator.sendKeys(Keys.F10);
				LOGGER.info("Key entered successfully" + keyName);

			case "f11":
				locator.sendKeys(Keys.F11);
				LOGGER.info("Key entered successfully" + keyName);

			case "f12":
				locator.sendKeys(Keys.F12);
				LOGGER.info("Key entered successfully" + keyName);
			}
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error  while entering key for the locator: " + locator + "Exception :" + e);
			String err = BrowserDriver.setScrnshoterrmsg("Error  while entering text for the locator :" + locator);
			JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.getDriver();
			js.executeScript(err);
			Assert.fail("Error  while entering key for the locator: " + locator + "Exception :" + e);

		}
	}
	
//return integer value for the textbox
	public static String statusCode(WebElement locator, String property) throws Exception {
		String refno = "";
		String status = locator.getAttribute(property).toString();
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(status);
		while (m.find()) {
			refno = m.group();
		}
		if (smhealer.isShflag()) {
			CollectElementDetails.elementDetails(locator);
		}
		return refno;

	}

	/**
	 * Gets current window handle
	 * 
	 * @return String value of current window handle
	 */
	public static String GetCurrentWindowHandle() {
		WebDriver driver = BrowserDriver.getDriver();
		return driver.getWindowHandle();

	}

//to be verified
	public static void swichToNewWindow() {
		WebDriver driver = BrowserDriver.getDriver();
		String parentWindow = driver.getWindowHandle();
		Set<String> allWindows = driver.getWindowHandles();
		for (String curWindow : allWindows) {
			if (!curWindow.equals(parentWindow)) {
				driver.switchTo().window(curWindow);
			}
		}

	}

	/**
	 * Closes current window
	 * 
	 * @param parent name or handle of parent window
	 */
	public static void closeCurrentWindow(String parent) {
		WebDriver driver = BrowserDriver.getDriver();
		driver.close();
		driver.switchTo().window(parent);
	}

	public static void sendEnter(WebElement locator) {
		try {
			locator.sendKeys(Keys.ENTER);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error while try to use Enter key for the element " + "Exception :" + e);
			BrowserDriver.setScrnshoterrmsg("Error while try to use Enter key for the element " + locator);
			Assert.fail("Error while try to use Enter key for the element " + "Exception :" + e);
		}
	}

	public static void setTableInputValue(WebElement locator, int columnIndex, int rowIndex, String value)
			throws InterruptedException, IOException {
		try {
			WebDriver driver = BrowserDriver.getDriver();
			Thread.sleep(3000);
			WebElement eleInput = driver
					.findElement(By.xpath("//table[contains(@aria-label,'All Items')]" + "//input[contains(@id,'"
							+ locator.getAttribute("id") + "[" + columnIndex + "," + rowIndex + "]_c')]"));
			eleInput.click();
			eleInput.sendKeys(value);
			eleInput.sendKeys(Keys.TAB);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error while try to set value for the table element " + "Exception :" + e);
			BrowserDriver.setScrnshoterrmsg("Error while try to set value for the table element " + locator);
			Assert.fail("Error while try to set value for the table element " + "Exception :" + e);
		}
	}

	public static void setTableSpanValue(WebElement locator, int columnIndex, int rowIndex, String value)
			throws InterruptedException, IOException {
		try {
			WebDriver driver = BrowserDriver.getDriver();
			Thread.sleep(3000);
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			WebElement eleInput = driver
					.findElement(By.xpath("//table[contains(@aria-label,'All Items')]" + "//span[contains(@id,'"
							+ locator.getAttribute("id") + "[" + columnIndex + "," + rowIndex + "]_c')]"));
			executor.executeScript("arguments[0].click();", eleInput);
			Thread.sleep(2000);
			WebElement eleAfterclick = driver
					.findElement(By.xpath("//table[contains(@aria-label,'All Items')]" + "//input[contains(@id,'"
							+ locator.getAttribute("id") + "[" + columnIndex + "," + rowIndex + "]_c')]"));
			eleAfterclick.sendKeys(value);
			if (smhealer.isShflag()) {
				CollectElementDetails.elementDetails(locator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error while try to set value for the table element " + "Exception :" + e);
			BrowserDriver.setScrnshoterrmsg("Error while try to set value for the table element " + locator);
			Assert.fail("Error while try to set value for the table element " + "Exception :" + e);
		}

	}

	public static void WriteExcel(String excelFilePath, String sheetName, Set<String> links) {
		HashMap<String, Integer> columns = new HashMap<String, Integer>();
		try {
			List<String> listCollection = new ArrayList<String>();
			listCollection.addAll(links);
			FileInputStream fis = new FileInputStream(excelFilePath);
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			XSSFCell cell;
			sheet.getRow(0).forEach(cell1 -> {
				columns.put(cell1.getStringCellValue(), cell1.getColumnIndex());
			});

			for (Map.Entry<String, Integer> entry : columns.entrySet()) {

				if (entry.getKey().contains("Links")) {
					for (int i = 1; i < listCollection.size(); i++) {
						XSSFRow row = sheet.getRow(i);
						if (row == null) {
							row = sheet.createRow(i);
						}
						cell = row.getCell(0);
						cell.setCellValue(listCollection.get(i).toString());
					}
				}
			}

			FileOutputStream out = new FileOutputStream(new File(excelFilePath));
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	public static void WriteExcelSequentialOrder(String excelFilePath, String sheetName, LinkedHashSet<String> links) {
		try {

			List<String> listCollection = new ArrayList<>(links);

			FileInputStream fis = new FileInputStream(excelFilePath);
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				sheet = workbook.createSheet(sheetName);
			}

			HashMap<String, Integer> columns = new HashMap<>();
			if (sheet.getRow(0) != null) {
				sheet.getRow(0).forEach(cell -> {
					columns.put(cell.getStringCellValue(), cell.getColumnIndex());
				});
			}

			int linksColumnIndex = columns.getOrDefault("Links", sheet.getRow(0) == null ? 0 : columns.size());
			if (!columns.containsKey("Links")) {
				XSSFRow headerRow = sheet.getRow(0) == null ? sheet.createRow(0) : sheet.getRow(0);
				XSSFCell headerCell = headerRow.createCell(linksColumnIndex);
				headerCell.setCellValue("Links");
			}

			for (int i = 0; i < listCollection.size(); i++) {
				XSSFRow row = sheet.getRow(i + 1);
				if (row == null) {
					row = sheet.createRow(i + 1);
				}
				XSSFCell cell = row.getCell(linksColumnIndex);
				if (cell == null) {
					cell = row.createCell(linksColumnIndex);
				}
				cell.setCellValue(listCollection.get(i));
			}

			FileOutputStream out = new FileOutputStream(new File(excelFilePath));
			workbook.write(out);
			out.close();
			System.out.println("Excel file updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public static void updateExecutionStatus(String excelFilePath, String sheetName, List<String> executedUrls,
			String status) {
		try {
			// Open the existing Excel file
			FileInputStream fis = new FileInputStream(excelFilePath);

			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			XSSFSheet sheet = workbook.getSheet(sheetName);

			if (sheet == null) {
				System.out.println("Sheet " + sheetName + " does not exist.");
				return;
			}

			HashMap<String, Integer> columns = new HashMap<>();
			XSSFRow headerRow = sheet.getRow(0);
			if (headerRow == null) {
				System.out.println("No headers found in the sheet.");
				return;
			}

			headerRow.forEach(cell -> columns.put(cell.getStringCellValue(), cell.getColumnIndex()));

			if (!columns.containsKey("Links") || !columns.containsKey("Execution Status")) {
				System.out.println("'Links' or 'Execution Status' column is missing.");
				return;
			}

			int linksColumnIndex = columns.get("Links");
			int statusColumnIndex = columns.get("Execution Status");

			Set<String> executedUrlsSet = new HashSet<>(executedUrls);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1 to skip the header
				XSSFRow row = sheet.getRow(i);
				if (row == null)
					continue;

				XSSFCell linksCell = row.getCell(linksColumnIndex);
				if (linksCell == null)
					continue; // Skip rows without a "Links" cell

				String link = linksCell.getStringCellValue();
				if (link == null || link.trim().isEmpty())
					continue;

				XSSFCell statusCell = row.getCell(statusColumnIndex);
				if (statusCell == null) {
					statusCell = row.createCell(statusColumnIndex);
				}

				if (executedUrlsSet.contains(link)) {
					statusCell.setCellValue(status);
				}

			}

			FileOutputStream out = new FileOutputStream(new File(excelFilePath));
			workbook.write(out);
			out.close();
			workbook.close();

			System.out.println("Execution status updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings("resource")
	public static List<String> ReadExcel(String excelFilePath, String sheetName) throws IOException {
		HashMap<String, String> Links = new HashMap<String, String>();
		new HashMap<String, Integer>();
		List<String> selectedLinks = new ArrayList<>();
		FileInputStream fis = new FileInputStream(excelFilePath);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet(sheetName);
		XSSFCell cell;
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			cell = sheet.getRow(i).getCell(0);
			String link = cell.getStringCellValue();
			cell = sheet.getRow(i).getCell(1);
			String status = cell.getStringCellValue();
			Links.put(link, status);
		}

		for (Map.Entry<String, String> entry : Links.entrySet()) {
			if (entry.getValue().equalsIgnoreCase("Selected")) {
				selectedLinks.add(entry.getKey());
				// System.out.println("Key = " + entry.getKey() + ", Value = " +
				// entry.getValue());
			}
		}
		return selectedLinks;
	}

	public static void runAccessibilityTest(List<String> value) {
		value.stream().sequential().forEach(url -> {
			try {
				WebHandlers.generateReport1(url);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		AccessibilityReportEngine reportEngine = new AccessibilityReportEngine();
		reportEngine.generateOverallAccessibilityReports();
	}
	
	public static void accessibilityTestAsParallel(String url) {

		try {
			WebHandlers.generateReport1(url);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Map<String, String> urlStatusMap = Collections.synchronizedMap(new HashMap<>());

	public static void runAccessibilityTestAsParallel(String excelFilePath, String sheetName, List<String> urls) {

		clearExecutionStatusColumn(cdir + "/src/main/resources/DataFiles/Excel/Links.xlsx", "LinkValue",
				"Execution Status");
		int numThreads = 10;
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);

		try {

			for (String url : urls) {

				executor.submit(() -> {
					try {
						accessibilityTestAsParallel(url);

					} catch (Exception e) {

						System.err.println("Error processing URL: " + url);
						e.printStackTrace();
					}
				});

			}

		} finally {

			executor.shutdown();
			while (!executor.isTerminated()) {

			}

			for (Map.Entry<String, String> entry : urlStatusMap.entrySet()) {
				String url = entry.getKey();
				String status = entry.getValue();

				WebHandlers.updateExecutionStatus(excelFilePath, sheetName, List.of(url), status);
				System.out.println("Updated execution status for URL: " + url + " as " + status);
			}

			AccessibilityReportEngine reportEngine = new AccessibilityReportEngine();
			reportEngine.generateOverallAccessibilityReports();
			System.out.println("Run Complete");
		}

	}

	public static void clearExecutionStatusColumn(String excelFilePath, String sheetName, String columnHeader) {
		try (FileInputStream fis = new FileInputStream(excelFilePath); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				System.out.println("Sheet not found: " + sheetName);
				return;
			}

			XSSFRow headerRow = sheet.getRow(0);
			if (headerRow == null) {
				System.out.println("Header row is missing in the sheet.");
				return;
			}

			int columnIndex = -1;
			for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
				XSSFCell cell = headerRow.getCell(cellIndex);
				if (cell != null && columnHeader.equals(cell.getStringCellValue())) {
					columnIndex = cellIndex;
					break;
				}
			}

			if (columnIndex == -1) {
				System.out.println("Column header not found: " + columnHeader);
				return;
			}

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				XSSFRow row = sheet.getRow(i);
				if (row != null) {
					XSSFCell cell = row.getCell(columnIndex);
					if (cell != null) {
						cell.setBlank();
					}
				}
			}

			try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
				workbook.write(fos);
			}

			System.out.println("Data under the '" + columnHeader + "' column has been cleared successfully.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static void videoRecorderStart(String testcaseName) throws Exception {
//		// "RecordedVideo-“+dateFormat.format(date)"
//
//		File file = new File(System.getProperty("user.dir") + "/SkriptmateReport/Videos/");
//
//		if (!file.exists()) {
//			FileUtils.makeDirs(System.getProperty("user.dir") + "/SkriptmateReport/Videos/");
//		}
//		try {
//			recorder = new ATUTestRecorder(cdir + "/SkriptmateReport/Videos", testcaseName + CurrentTime(), false);
//			recorder.start();
//			LOGGER.info("Video recording started successfully");
//		} catch (ATUTestRecorderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public static void videoRecorderStop() {
//		try {
//			recorder.stop();
//			LOGGER.info("Video recording stopped successfully");
//		} catch (ATUTestRecorderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//			LOGGER.info("Error to stop the video recording" + e.getMessage());
//		}
//	}

	public static void generateReport1(String url) throws InterruptedException, IOException {
		PropertyDriver p = new PropertyDriver();
		p.setPropFilePath("src/main/resources/Web/Properties/Browser.properties");
		final WebDriver driver1;
		Properties props = new Properties();
		FileInputStream inputStream = new FileInputStream(PropertyDriver.getFilePath());
		props.load(inputStream);
		System.setProperty("webdriver.chrome.silentOutput", "true");
		System.setProperty("webdriver.chrome.driver", cdir + "/src/main/resources/Web/Drivers/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		options.addArguments("enable-automation");
		// options.addArguments("--headless"); // only if you are ACTUALLY running
		// headless
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-browser-side-navigation");
		options.addArguments("--disable-gpu"); // how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc
		options.addArguments("enable-automation");
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--disable-extensions");
		options.addArguments("--dns-prefetch-disable");
		options.addArguments("--remote-allow-origins=*");
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

		if (!PropertyDriver.readProp("https.proxyHost").isEmpty()
				&& !PropertyDriver.readProp("https.proxyPort").isEmpty()) {
			String Host = PropertyDriver.readProp("https.proxyHost");
			String Port = PropertyDriver.readProp("https.proxyPort");
			String proxy = "" + Host + " " + ":" + Port + "";
			options.addArguments("--proxy-server=http://" + proxy);
		}
		driver1 = new ChromeDriver(options);
		System.out.println("Accessibility test for url:- " + url);
		driver1.get(url);

		if (url.contains(driver1.getCurrentUrl())) {
			Accessibility access = new Accessibility(driver1);
			//access.generateAccessibilityReportForCurrentPage(driver1.getCurrentUrl());
			//access.generateAccessibilityReportForCurrentPagesParallelly(driver1.getCurrentUrl(),
			// driver1);
			driver1.quit();
			urlStatusMap.put(url, "completed");

			System.out.println("Execution completed: " + url);

		}

		else {
			urlStatusMap.put(url, "Not executed");

			driver1.quit();
			System.out.println("Execution Not completed: " + url);

		}


	}

	public static String CurrentTime() {
		String pattern = "yy-MM-dd-HH-mm-ss-SSSZ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		return date;
	}

	public static void storeAppText() {

		try {
			List<WebElement> header = BrowserDriver.getDriver().findElements(By.tagName("h2"));
			if (!header.isEmpty() || header != null) {
				for (WebElement divs : header) {
					String divsCorrect = divs.getText().replaceAll("&nbsp", "").replaceAll(" ", "").replaceAll("\n", "")
							.trim();
					if (!divsCorrect.isEmpty() && divsCorrect != null && divsCorrect != "") {
						headerDetails.add(divsCorrect);
					}
				}
			}
		} catch (Exception e) {
			// ignore
		}
		try {
			List<WebElement> span = BrowserDriver.getDriver().findElements(By.tagName("span"));
			if (!span.isEmpty() || span != null) {
				for (WebElement spans : span) {
					String spanCorrect = spans.getText().replaceAll("&nbsp;", "").replaceAll(" ", "")
							.replaceAll("\n", "").trim();
					if (!spanCorrect.isEmpty() && spanCorrect != null && spanCorrect != "") {
						spanDetails.add(spanCorrect);
					}
				}
			}
		} catch (Exception e) {
			// ignore
		}
		try {
			List<WebElement> para = BrowserDriver.getDriver().findElements(By.tagName("p"));
			if (!para.isEmpty() || para != null) {
				for (WebElement parass : para) {
					String parassCorrect = parass.getText().replaceAll("&nbsp", "").replaceAll(" ", "")
							.replaceAll("\n", "").trim();
					if (!parassCorrect.isEmpty() && parassCorrect != null && parassCorrect != "") {
						paraDetails.add(parassCorrect);
					}
				}
			}
		} catch (Exception e) {
			// ignore
		}

	}

	public static void checkAppValueWithPDF(String PdfName) throws Exception {

		try {
			String path = pdfPath();
			if (path == null) {
				throw new Exception("PDF not available");
			} else if (path != null) {

				String PdfPath = path + "\\" + PdfName + ".pdf";
				List<String> failedetails = new ArrayList<String>();

				StringBuffer value = readParaFromPDF(PdfPath);

				if (spanDetails != null || !spanDetails.isEmpty()) {
					for (String i : spanDetails) {
						// Printing the elements of ArrayList
						String test = i.replaceAll("[^\\p{ASCII}],", "").trim();
						String pdfdata = value.toString().replaceAll("[^\\p{ASCII}],", "").trim();
						if (pdfdata.contains(test)) {
						} else if (!value.toString().trim().contains(test)) {
							failedetails.add(i);
						}
					}
				}

				if (headerDetails != null || !headerDetails.isEmpty()) {
					for (String i : headerDetails) {
						// Printing the elements of ArrayList
						String test = i.replaceAll("[^\\p{ASCII}],", "").trim();
						String pdfdata = value.toString().replaceAll("[^\\p{ASCII}],", "").trim();
						if (pdfdata.contains(test)) {
						} else if (!value.toString().trim().contains(test)) {
							failedetails.add(i);
						}
					}
				}

				if (paraDetails != null || !paraDetails.isEmpty()) {
					for (String i : paraDetails) {
						// Printing the elements of ArrayList
						String test = i.replaceAll("[^\\p{ASCII}],", "").trim();
						String pdfdata = value.toString().replaceAll("[^\\p{ASCII}],", "").trim();
						if (pdfdata.contains(test)) {
						} else if (!value.toString().trim().contains(test)) {
							failedetails.add(i);
						}
					}
				}

				if (failedetails.size() == 0) {
					System.out.println("PDF and form verification completed " + "" + "with out any error");
					LOGGER.info("PDF and form verification completed " + "" + "with out any error");
				} else {
					LOGGER.info("PDF and form verification completed with defect Count: " + failedetails.size()
							+ " with details: " + failedetails);

					SoftAssert objAssert = new SoftAssert();
					objAssert.assertTrue(false, "PDF and form verification completed with defect Count: "
							+ failedetails.size() + " " + "with details: " + failedetails);
					objAssert.assertAll();
				}
			}
		} catch (Exception e) {
			LOGGER.info("Error for PDF verification");
		}

	}

	public static String pdfPath() {
		PropertyDriver p = new PropertyDriver();
		String absolutePath = null;
		p.setPropFilePath("src/main/resources/Web/Properties/Browser.properties");
		File path = PropertyDriver.getFilePath();
		if (path.exists() && !path.isDirectory()) {
			absolutePath = PropertyDriver.readProp("download");
		}
		return absolutePath;
	}

	public static StringBuffer readParaFromPDF(String pdfPath) throws Exception {
		String pdfFileInText = null;

		StringBuffer pdfData = new StringBuffer();

		try {

			File file = new File(pdfPath);
			PDDocument document = Loader.loadPDF(file);

			document.getClass();
			document.getPages();

			// Create PDFTextStripper - used for searching the page string
			PDFTextStripper textStripper = new PDFTextStripper();
			PDPageTree page = document.getPages();
			int startPage = 1;
			int endPage = page.getCount();
			// Set textStripper to search one page at a time
			textStripper.setStartPage(startPage);
			textStripper.setEndPage(endPage);
			// Fetch page text and insert into "pages" string
			pdfFileInText = textStripper.getText(document);
			pdfData.append(pdfFileInText);

		} catch (Exception e) {
			throw new Exception("Error while try to get text from PDF File" + e.getMessage());
		}

		return pdfData;
	}

	public static void highlight(WebElement element) throws InterruptedException {
		String highlighter = "!function(){var e=document.createElement(\"style\");styles=\".nsHighlight {outline:2px solid #FF4933 !important}\",e.innerHTML=styles,document.documentElement.appendChild(e)}(),function(){const e=document.createElement(\"script\");e.text='function highlight(element) {element.classList.add(\"nsHighlight\");}function unhighlight(element) {element.classList.remove(\"nsHighlight\");}',e.type=\"text/javascript\",document.head.appendChild(e)}();";
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript(highlighter);
		jse.executeScript("highlight(arguments[0]);", element);
		Thread.sleep(500);
		jse.executeScript("unhighlight(arguments[0]);", element);
		Thread.sleep(500);
	}

	public void evaluateXpath(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) BrowserDriver.driver;
		@SuppressWarnings("unused")
		Object out = js.executeScript(
				"return document.evaluate(arguments[0], document , null, 'XPathResult.FIRST_ORDERED_NODE_TYPE' , null)",
				element);
	}

	public static Set<String> getAlltheValidLinksforWebPage() {
		Set<String> linksActual = new HashSet<>();
		try {

			List<WebElement> links = BrowserDriver.getDriver().findElements(By.tagName("a"));
			System.out.println("size of the links" + links.size());
			for (WebElement linkscheck : links) {
				String url = linkscheck.getAttribute("href");

				org.apache.commons.validator.routines.UrlValidator urlValidator = new org.apache.commons.validator.routines.UrlValidator();
				if (urlValidator.isValid(url)) {
					linksActual.add(url);
				}
			}

			List<WebElement> link1 = BrowserDriver.getDriver().findElements(By.xpath("//navbar-menu-item/a"));
			System.out.println("size of the secondlinks" + link1.size());
			for (WebElement linkscheck1 : link1) {
				String url1 = linkscheck1.getAttribute("href");
				new org.apache.commons.validator.routines.UrlValidator();
				linksActual.add(url1);
			}
		} catch (Exception e) {
		}
		return linksActual;
	}

}
