package com.scripted.truehealing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class testjs {
	
	public static void main(String[] args) throws IOException {
		FileInputStream inputStreamshub = new FileInputStream(new File(
				"C:\\Selenium4\\SkriptmateRepo4\\TAF-Scripting\\src\\main\\resources\\HealingConfigurations\\selectorshub.js"));
		System.out.println(IOUtils.toString(inputStreamshub));
	}

}
