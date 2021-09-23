package com.newrelic.nri.tibco.ems;

public class EntityData {

	private String name;
	private StatType statType;
	
	private EMSMetrics metrics;
	
	public EntityData(String n, StatType st, EMSMetrics m) {
		name = n;
		statType = st;
		metrics = m;
	}

	public String getName() {
		return name;
	}

	public StatType getStatType() {
		return statType;
	}

	public EMSMetrics getMetrics() {
		return metrics;
	}
	
	
}
