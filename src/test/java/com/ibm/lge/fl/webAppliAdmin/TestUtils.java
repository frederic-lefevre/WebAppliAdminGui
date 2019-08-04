package com.ibm.lge.fl.webAppliAdmin;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.RunningContext;

public class TestUtils {

	private static final String DEFAULT_PROP_FILE = "webAppliAdmin.properties" ;
	
	private static RunningContext 	  runningContext ;
	private static AdvancedProperties apiProperties ;
	
	public static RunningContext getRunningContext() {
		
		if (runningContext == null) {
			runningContext = new RunningContext("Administration for web applications", null, DEFAULT_PROP_FILE);
		}
		apiProperties   = runningContext.getProps().getPropertiesFromFile("webAppli.configurationFile") ;
		
		return runningContext ;

	}
	
	public static AdvancedProperties getApiProperties() {
		
		if (apiProperties == null) {
			getRunningContext() ;
		}
		return apiProperties ;
	}
}
