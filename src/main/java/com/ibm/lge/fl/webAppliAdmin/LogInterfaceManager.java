package com.ibm.lge.fl.webAppliAdmin;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.ibm.lge.fl.util.AdvancedProperties;

public class LogInterfaceManager {

	private final Vector<LogInterface> logInterfaces ;
	
	public LogInterfaceManager(AdvancedProperties props, String baseProperty, Logger log) {
		
		logInterfaces = new Vector<LogInterface>() ;
		
		List<String> logsProperties = props.getKeysElements(baseProperty);
		for (String lp : logsProperties) {
			logInterfaces.add(new LogInterface(props, baseProperty + lp, log)) ;
    	}
	}

	public Vector<LogInterface> getLogInterfaces() {
		return logInterfaces ;
	}
}
