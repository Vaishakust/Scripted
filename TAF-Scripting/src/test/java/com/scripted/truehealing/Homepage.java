package com.scripted.truehealing;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.scripted.web.WebHandlers;

import bsh.EvalError;

public class Homepage {
	

	private WebDriver driver;
	

	@FindBy(xpath = "//*[@id='TY567']")
	private WebElement button_ShowMessage;

	public Homepage(WebDriver driver) {
		this.driver = driver;
	}
	
	public void entervalueandClick() throws InterruptedException, IOException, EvalError {

		WebHandlers.clickByJsExecutor(button_ShowMessage);
	}

}
