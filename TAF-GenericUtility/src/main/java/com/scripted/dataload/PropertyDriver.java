package com.scripted.dataload;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.scripted.generic.FileUtils;


public class PropertyDriver {
	public static Logger LOGGER = LogManager.getLogger(PropertyDriver.class);
	public static File filePath;
	public static File jsonfilePath;
	public static String JsonFilePath;

	// JsonFile Path
	public static String getJsonFilePath() {
		return JsonFilePath;
	}

	public static void setJsonFilePath(String jsonFilePath) {
		JsonFilePath = jsonFilePath;
	}

	/**
	 * Constructors based on input parameter type
	 * 
	 * @param filename - full name of the property file
	 */

	@SuppressWarnings("static-access")
	public void setPropFilePath(String filename) {
		File filePath = new File(FileUtils.getFilePath(filename));
		this.filePath = filePath;
	}

	public static File getFilePath() {
		return filePath;
	}

	/**
	 * Constructors based on input parameter type
	 * 
	 * @param filename - full name of the json file
	 */

	@SuppressWarnings("static-access")
	public void setCucumberJsonFilePath(String jsonFile) {
		File jsonfilePath = new File(FileUtils.getFilePath(jsonFile));
		this.jsonfilePath = jsonfilePath;
	}

	public static File getCucumberJsonFilePath() {
		return jsonfilePath;
	}

	/**
	 * Read the property value based on key
	 * 
	 * @param key - identification key
	 */

	public static String readProp(String key) {
		try (FileReader reader = new FileReader(filePath)) {
			Properties pf = new Properties();
			pf.load(reader);
			return pf.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error while reading property file" + "Exception :" + e);
			Assert.fail("Error while reading property file" + "Exception :" + e);
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> readProp() {
		Map<String, String> propMap = new HashMap();
		try (FileReader reader = new FileReader(filePath)) {
			Properties pf = new Properties();
			pf.load(reader);
			propMap.putAll(pf.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.printStackTrace();
			LOGGER.error("Error while reading property file");
			Assert.fail("Error while reading property file" + "Exception :" + e);
		}

		return propMap;
	}

}
