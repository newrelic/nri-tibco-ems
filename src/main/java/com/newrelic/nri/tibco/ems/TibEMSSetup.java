package com.newrelic.nri.tibco.ems;

import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class TibEMSSetup {

	public static void main(String[] args) {
		if(args.length > 0) {
			String password = args[0];
			try {
				String mangled = TibjmsAdmin.manglePassword(password);
				System.out.println("Mangled password: "+ mangled);
			} catch (TibjmsAdminException e) {
				System.out.println("Failed to mangle password due to error");
				e.printStackTrace();
			}
		} else {
			System.out.println("Usage:");
			System.out.println("\t java com.newrelic.nri.tibco.ems.TibEMSSetup passwordToMangle");
		}
	}

}
