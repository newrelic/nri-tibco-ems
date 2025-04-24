package com.newrelic.nri.tibco.ems;

import java.util.Vector;

public class SSLConfig {

	private String vendor = null;
	private boolean trace = false;
	private boolean debug_trace = false;
	private boolean verifyHost = false;
	private boolean verifyHostname = false;
	private String expectedHostname = null;
	private Vector<String> trusted = null;
	private Vector<String> issuers = null;
	private String identity = null;
	private String privateKey = null;
	private String password = null;
	private boolean custom = false;
	private String ciphers = null;
	
	public String getVendor() {
		return vendor;
	}
	
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	public boolean isTrace() {
		return trace;
	}
	
	public void setTrace(boolean trace) {
		this.trace = trace;
	}
	
	public boolean isDebug_trace() {
		return debug_trace;
	}
	
	public void setDebug_trace(boolean debug_trace) {
		this.debug_trace = debug_trace;
	}
	
	public boolean isVerifyHostname() {
		return verifyHostname;
	}
	
	public void setVerifyHostname(boolean verifyHostname) {
		this.verifyHostname = verifyHostname;
	}
	
	public String getExpectedHostname() {
		return expectedHostname;
	}
	
	public void setExpectedHostname(String expectedHostname) {
		this.expectedHostname = expectedHostname;
	}
	
	public Vector<String> getTrusted() {
		return trusted;
	}
	
	public void addTrusted(String trust) {
		if(trusted == null) {
			trusted = new Vector<>();
		}
		trusted.add(trust);
	}
	
	public Vector<String> getIssuers() {
		return issuers;
	}
	
	public void addIssuers(String issuer) {
		if(issuers == null) {
			issuers = new Vector<>();
		}
		this.issuers.add(issuer);
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public String getPrivateKey() {
		return privateKey;
	}
	
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isCustom() {
		return custom;
	}
	
	public void setCustom(boolean custom) {
		this.custom = custom;
	}
	
	public String getCiphers() {
		return ciphers;
	}
	
	public void setCiphers(String ciphers) {
		this.ciphers = ciphers;
	}
	
	public void setVerifyHost(boolean verify) {
		this.verifyHost = verify;
	}
	
	public boolean isVerifyHost() {
		return verifyHost;
	}
}
