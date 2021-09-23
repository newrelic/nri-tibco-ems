package com.newrelic.nri.tibco.ems;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class EMSMonitorFactory {

	private static final int DEFAULT_PORT = 7222;

	public EMSMonitor createAgent(Map<String, Object> properties) throws Exception {
		String name = (String) properties.get("name");
		String host = (String) properties.get("host");
		Number port = (Number) properties.get("port");
		if (port == null) {
			port = new Integer(DEFAULT_PORT);
		}
		String username = (String) properties.get("username");
		String password = (String) properties.get("password");

		EMSServer ems = new EMSServer(name, host, port.intValue(), username, password);

		ems.setFlagIncludeDynamicQueues((Boolean) properties.get("includeDynamicQueues"));

		ArrayList<Object> qIgnores = (ArrayList<Object>) properties.get("queueIgnores");

		if (qIgnores != null) {
			for (int k = 0; k < qIgnores.size(); k++) {
				LinkedHashMap<String, String> regex = (LinkedHashMap<String, String>) qIgnores.get(k);
				if (regex != null) {
					String val = (String) regex.get("qIgnoreRegEx");
					if (val != null && !val.isEmpty()) {
						ems.addToQueueIgnores(val);
					}
				}
			}
		}

		ems.setFlagIncludeDynamicTopics((Boolean) properties.get("includeDynamicTopics"));
		/*
		 * JSONArray tIgnores = (JSONArray) properties.get("topicIgnores"); if (tIgnores
		 * != null) { for (int i = 0; i < tIgnores.size(); i++) { JSONObject regex =
		 * (JSONObject)qIgnores.get(i); if(regex != null) { String val = (String)
		 * regex.get("tIgnoreRegEx"); if(val != null && !val.isEmpty()) {
		 * ems.addToTopicIgnores(val); } } } }
		 */
		EMSMonitor agent = new EMSMonitor(ems);

		return agent;
	}

}
