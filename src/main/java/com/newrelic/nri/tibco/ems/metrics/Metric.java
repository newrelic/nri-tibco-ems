package com.newrelic.nri.tibco.ems.metrics;

public interface Metric {

	public SourceType getSourceType();
	
	public String getName();
	
	public Object getValue();

}
