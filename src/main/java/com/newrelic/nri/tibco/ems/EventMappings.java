package com.newrelic.nri.tibco.ems;

import java.util.Hashtable;
import java.util.Map;

public class EventMappings {

	private static final Map<String, String> mappings = new Hashtable<>();
	
	static {
		mappings.put("Channel", "EMSChannel");
		mappings.put("ChannelDetails", "EMSChannelDetails");
		mappings.put("Bridge", "EMSBridge");
		mappings.put("Queue", "EMSQueue");
		mappings.put("Route", "EMSRoute");
		mappings.put("Server", "EMSServer");
		mappings.put("Topic", "EMSTopic");
		mappings.put("QueueTotals", "EMSQueueTotals");
		mappings.put("TopicTotals", "EMSTopicTotals");
		mappings.put("RouteTotals", "EMSRouteTotals");
	}
	
	public static String getEventMapping(String name) {
		return mappings.get(name);
	}
	
	public static void setEventMapping(String name, String value) {
		mappings.put(name, value);
	}
	
	public static Map<String,String> getMappings() {
		return mappings;
	}
}
