package com.scripted.PDF;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;


public class CucumberJsonExtractor {
	String PASS = "passed";
	String FAIL = "failed";

	public static Logger LOGGER = LogManager.getLogger(CucumberJsonExtractor.class);

	private JSONArray readData(String path) throws IOException {
		JSONArray cucumberJson = null;
		File file = new File(path);
		try {
			String content = FileUtils.readFileToString(file, "utf-8");
			cucumberJson = new JSONArray(content);

		} catch (Exception e) {
			LOGGER.error("Error while trying to read file " + "Exception :" + e);
			Assert.fail("Error while trying to read file " + "Exception :" + e);
		}
		return cucumberJson;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public JSONObject getScenarioAndStepsStatus(String cucumberJsonPath) throws IOException {
		JSONArray jsonArray = readData(cucumberJsonPath);
		JSONObject tagObject = new JSONObject();
		@SuppressWarnings("unchecked")
		Map<String, JSONObject> resultmap = new HashedMap();
		try {

			for (Object object : jsonArray) {
				
				JSONObject obj = (JSONObject) object;
				JSONArray scenario = obj.getJSONArray("elements");
				for (Object arr : scenario) {
					JSONObject details = new JSONObject();
					JSONObject scenarioObj = (JSONObject) arr;
					JSONArray stepsArray = scenarioObj.getJSONArray("steps");
					JSONArray tags = scenarioObj.getJSONArray("tags");
					JSONArray stepStatusList = new JSONArray();
					details.put("scenarioStatus", PASS);
					for (Object stepObj : stepsArray) {
						JSONObject steps = (JSONObject) stepObj;
						String stepStatus = steps.getJSONObject("result").getString("status");
						if (stepStatus.equals("failed")) {
							String stepErrorDetails = steps.getJSONObject("result").getString("error_message");
							stepErrorDetails = stepErrorDetails.replaceAll("[\\n\\t\\r\"]", "");
							if(stepErrorDetails.length()>700)
								stepErrorDetails = stepErrorDetails.substring(0, 700);
							details.put("scenarioStatus", FAIL);
							details.put("scenarioError", stepErrorDetails);
						}
						stepStatusList.put(stepStatus);
					}
					details.put("stepStatus", stepStatusList);
					tagObject.put(scenarioObj.getString("name"), details);
					
			}
			}
		} catch (Exception e) {
			LOGGER.error("Error while trying to get status of steps and scenarios " + "Exception :" + e);
			Assert.fail("Error while trying to get status of steps and scenarios " + "Exception :" + e);
		}
		return tagObject;
	
	}
}
