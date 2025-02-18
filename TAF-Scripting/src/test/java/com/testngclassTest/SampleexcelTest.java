package com.testngclassTest;

import java.util.LinkedHashMap;
import java.util.Map;

import com.scripted.dataload.ExcelConnector;
import com.scripted.dataload.PropertyDriver;
import com.scripted.dataload.TestDataFactory;
import com.scripted.dataload.TestDataObject;
import com.scripted.generic.FileUtils;

public class SampleexcelTest {
	
	public static Map<String, String> fAutoRow;
	static PropertyDriver propDriver = new PropertyDriver();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		propDriver.setPropFilePath("src/main/resources/DataFiles/Properties/config.properties");
		String filename = FileUtils.getCurrentDir() + PropertyDriver.readProp("DataSheetName");
		String sheetname = "TC001";
		String key = "";
		ExcelConnector con = new ExcelConnector(filename, sheetname);
		TestDataFactory test = new TestDataFactory();
		TestDataObject obj = test.GetData(key, con);
		LinkedHashMap<String, Map<String, String>> getAllData = obj.getTableData();
		fAutoRow = (getAllData.get("1"));
		
		 for (Map.Entry<String,String> entry : fAutoRow.entrySet())  
	            System.out.println("Key = " + entry.getKey() + 
	                             ", Value = " + entry.getValue()); 
	    } 
	}

