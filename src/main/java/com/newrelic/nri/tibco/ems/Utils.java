package com.newrelic.nri.tibco.ems;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Utils {
	private static final String NAME = "Tibco_EMS";
	JSONArray dataArray = new JSONArray();

	private static final String PROTOCOL_VERSION = Integer.toString(2);
	private static final String INTEGRATION_VERSION = "1.0";

	@SuppressWarnings("unchecked")
	public static void reportError(String msg, Throwable t) {
		JSONObject json = new JSONObject();
		json.put("name", NAME);
		json.put("protocol_version", PROTOCOL_VERSION);
		json.put("integration_version", INTEGRATION_VERSION);

		JSONArray dataArray = new JSONArray();
		JSONObject json2 = new JSONObject();
		JSONObject entity = new JSONObject();
		entity.put("name", "TibcoEMSError");
		entity.put("type", "Error");
		json2.put("entity", entity);

		JSONArray metrics = new JSONArray();
		json2.put("metrics", metrics);
		JSONObject inventory = new JSONObject();
		json2.put("inventory", inventory);
		JSONArray errors = new JSONArray();
		JSONObject error = new JSONObject();
		StringWriter str_writer = new StringWriter();
		PrintWriter writer = new PrintWriter(str_writer);
		t.printStackTrace(writer);
		String errorMsg = t.getMessage();
		error.put("message", msg);
		error.put("error message", errorMsg);
		error.put("Stacktrace", str_writer.toString());

		errors.add(error);
		json2.put("events", errors);
		dataArray.add(json2);

		json.put("data", dataArray);

		String jsonStr = json.toString();
		System.out.println(jsonStr);
	}

	@SuppressWarnings("unchecked")
	public static void reportError(String msg) {
		JSONObject json = new JSONObject();
		json.put("name", NAME);
		json.put("protocol_version", PROTOCOL_VERSION);
		json.put("integration_version", INTEGRATION_VERSION);

		JSONArray dataArray = new JSONArray();
		JSONObject json2 = new JSONObject();
		JSONObject entity = new JSONObject();
		entity.put("name", "TibcoEMSError");
		entity.put("name", "Error");
		json2.put("entity", entity);

		JSONArray metrics = new JSONArray();
		json2.put("metrics", metrics);
		JSONObject inventory = new JSONObject();
		json2.put("inventory", inventory);
		JSONArray errors = new JSONArray();
		JSONObject error = new JSONObject();
		error.put("message", msg);

		errors.add(error);
		json2.put("events", errors);
		dataArray.add(json2);

		json.put("data", dataArray);

		String jsonStr = json.toString();
		System.out.println(jsonStr);
	}

}
