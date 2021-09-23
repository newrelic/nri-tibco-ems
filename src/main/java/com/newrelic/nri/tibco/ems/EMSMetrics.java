package com.newrelic.nri.tibco.ems;

import java.util.HashMap;
import java.util.Map;

public class EMSMetrics {

	Map<String, Object> attributes = new HashMap<String, Object>();
	Map<String, Number> metrics = new HashMap<String, Number>();
	
	protected void addAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	protected void addMetric(String name, Number value) {
		metrics.put(name, value);
	}
	
	
	protected Map<String, Object> getAttributes() {
		return attributes;
	}
	
	protected Map<String,Number> getMetrics() {
		return metrics;
	}
	
	
}
