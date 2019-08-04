package com.ibm.lge.fl.webAppliAdmin;

import com.ibm.lge.fl.util.RunningContext;

public class TestUtils {

	private static final String DEFAULT_PROP_FILE = "webAppliAdmin.properties" ;
	
	private static RunningContext runningContext ;
	
	public static RunningContext getRunningContext() {
		
		if (runningContext == null) {
			runningContext = new RunningContext("Administration for web applications", null, DEFAULT_PROP_FILE);
		}
		// access to properties and logger
		return runningContext ;

	}
}
