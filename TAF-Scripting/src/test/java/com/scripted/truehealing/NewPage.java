package com.scripted.truehealing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.scripted.web.BrowserDriver;
import com.scripted.web.WebHandlers;

import bsh.EvalError;

public class NewPage {
	
	private WebDriver driver;

	public NewPage(WebDriver driver) {
		this.driver = driver;
	}

	@FindBy(xpath = "//*[@id='TY568']")
	private WebElement button_ShowMessage;
	
	@FindBy(xpath = "//*[@id='TY567']")
	private WebElement button_ShowMessage1;
	
	
	@FindBy(xpath = "//*[@id='user-message1234']")
	private WebElement input_Pleaseenteryour1;
	
	@FindBy(xpath = "//*[@id='user-message2']")
	private WebElement input_Pleaseenteryour2;
	
	@FindBy(xpath = "//*[@id='easycont']/div/div[2]/div[1]/div[2]/iframe")
	private WebElement iframe;
	
	@FindBy(xpath = "//*[@id=\"myTable\"]/tr[1]/td[2]/input")
	private WebElement tablecheck;
	
	@FindBy(xpath = "/html/body/div[2]/div/div[2]/section/div/table")
	private WebElement tablecheck2;
	
	@FindBy(xpath = "//*[@id=\"noImageRequiredForLoadTime\"]/div[1]/div[2]/div[2]/div/div[1]/div/div[1]/div[3]/div[1]/div/input")
	private WebElement username;
	
	
	@FindBy(xpath = "//*[@id=\"noImageRequiredForLoadTime\"]/div[1]/div[2]/div[2]/div/div[1]/div/div[1]/div[3]/div[2]/div/input")
	private WebElement password;
	
	@FindBy(xpath = "//*[@id=\"noImageRequiredForLoadTime\"]/div[1]/div[2]/div[2]/div/div[1]/div/div[1]/div[3]/button")
	private WebElement signin;
	
	@FindBy(xpath = "//*[@id=\"tdCheckbox\"]")
	private WebElement rememberdevice;
	
	@FindBy(xpath = "//*[@id=\"submitButton\"]")
	private WebElement submitbtn;

	@FindBy(xpath = "//div[@id='wd-searchInput']//child::input[1]")
	private WebElement searchoption;
	

	@FindBy(xpath = "//*[@id='wd-icon-justify']")
	private WebElement beradscrumb;
	
	public void entervalueandClick() throws InterruptedException, IOException, EvalError {
		Thread.sleep(2000);

//		WebHandlers.switchToFrame(iframe);
		WebHandlers.enterText(input_Pleaseenteryour1, "test");
		Thread.sleep(3000);
//		WebHandlers.clickByJsExecutor(button_ShowMessage);
//		WebHandlers.clickByJsExecutor(button_ShowMessage1);
//		Thread.sleep(12000);
//		
		
//		String rt = button_ShowMessage.getAttribute("outerHTML");
//		elementPropertyfromPagesource(rt, button_ShowMessage);
	}
	
	public void tableCheck() throws InterruptedException, IOException, EvalError {
		Thread.sleep(2000);
		System.out.println(WebHandlers.getTblTdVal(tablecheck2, 3,1));
	}
	
	public void workDayLogin() throws InterruptedException, IOException, EvalError {
		Thread.sleep(2000);
		WebHandlers.enterText(username , "A6834");
		Thread.sleep(3000);
		WebHandlers.enterText(password , "ZayahMaryam@2");
		WebHandlers.clickByJsExecutor(signin);
		Thread.sleep(6000);
		WebHandlers.clickByJsExecutor(rememberdevice);
		Thread.sleep(2000);
		WebHandlers.clickByJsExecutor(submitbtn);
		Thread.sleep(25000);
		WebHandlers.enterText(searchoption , "A6834");
		Thread.sleep(6000);
	}
}
