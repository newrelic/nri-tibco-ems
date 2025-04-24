package com.newrelic.nri.tibco.ems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;

// import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;
import com.tibco.tibjms.Tibjmsx;

public class EMSMonitorFactory {

	private static final Number DEFAULT_PORT = 7222;
	private static String MANGLED_PREFIX = "$man$";
	private static int MANGLED_PREFIX_LEN = MANGLED_PREFIX.length();


	public EMSMonitor createAgent(Map<String, Object> properties) throws Exception {
		String name = (String) properties.get("name");
		String host = (String) properties.get("host");
		Number port = (Number) properties.get("port");
		if (port == null) {
			port = DEFAULT_PORT;
		}
		String username = (String) properties.get("username");
		String password = (String) properties.get("password");
		Boolean encryptPassword = (Boolean) properties.get("encryptPassword");
		String protocol = ((String) properties.getOrDefault("protocol", "tcp")).toLowerCase();
		

		if (encryptPassword != null && encryptPassword) {
			try {
					String unmangledPassword = EMSMonitorFactory.unmanglePassword(password);
					password = unmangledPassword;
			} catch (TibjmsAdminException e) {
					e.printStackTrace();
			}
		} else {
			encryptPassword = false;
			}

		EMSServer ems = new EMSServer(name, host, port.intValue(), username, password, protocol);

		if(protocol.equalsIgnoreCase("ssl")) {
			SSLConfig sslConfig = (SSLConfig) properties.get("sslConfig");
			
			
		}
		
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
	private static String unmanglePassword(String mangedPassword) throws TibjmsAdminException {
		String string = null;
		byte[] arrby = (byte[]) Tibjmsx.base64Decode((String) mangedPassword.substring(MANGLED_PREFIX_LEN));
		if (arrby == null || arrby.length == 0) {
			throw new TibjmsAdminException("Empty buffer");
		}
		byte by = arrby[0];
		byte by2 = arrby[1];
		int n = (arrby[3] ^ by) << 8 & 65280 | (arrby[2] ^ by2) & 255;
		n &= 65535;
		if (arrby.length < (n -= 7777) + 4 || n < 0) {
			throw new TibjmsAdminException("Corrupt buffer");
		}
		byte[] arrby2 = new byte[n];
		System.arraycopy(arrby, 4, arrby2, 0, n);
		for (int i = 0; i < n; ++i) {
			byte by3 = i % 2 != 0 ? by2 : by;
			arrby2[i] = (byte) (arrby2[i] ^ by3);
			int n2 = by3 & 1;
			by3 = (byte) (by3 >> 1);
			by3 = n2 != 0 ? (byte) (by3 | 128) : (byte) (by3 & 127);
			if (i % 2 != 0) {
				by2 = by3;
				continue;
			}
			by = by3;
		}
		try {
			string = new String(arrby2, "UTF8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new TibjmsAdminException("Could not convert password to UTF8",
					(Exception) unsupportedEncodingException);
		}
		return string;
	}


}
