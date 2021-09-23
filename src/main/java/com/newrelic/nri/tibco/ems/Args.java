package com.newrelic.nri.tibco.ems;

import com.beust.jcommander.Parameter;

public class Args {
	@Parameter(names = "--help", description = "This usage help", help = true)
	private boolean help;

	public boolean isHelp() {
		return help;
	}

	@Parameter(names = "-config_file", description = "Path to config file")
	private String configFile = null;

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}




}