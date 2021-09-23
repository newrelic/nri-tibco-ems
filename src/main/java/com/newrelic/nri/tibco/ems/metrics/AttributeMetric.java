package com.newrelic.nri.tibco.ems.metrics;

public class AttributeMetric implements Metric {
	String name;
	Object value;

	public AttributeMetric(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public SourceType getSourceType() {
		return SourceType.ATTRIBUTE;
	}

	@Override
	public String toString() {
		return "AttributeMetric [name=" + getName() + ", value=" + getValue() + "]";
	}

}