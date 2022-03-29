package org.fl.webAppliAdmin;

import java.util.List;
import java.util.Vector;

import com.ibm.lge.fl.util.AdvancedProperties;

public class HostManager {

	private final Vector<Host> hosts ;
	
	public HostManager(AdvancedProperties props, String baseProperty) {
		
		hosts = new Vector<Host>() ;
		
		List<String> logsProperties = props.getKeysElements(baseProperty);
		for (String lp : logsProperties) {
			String address 	= props.getProperty(baseProperty + lp + ".address") ;
			String appPath	= props.getProperty(baseProperty + lp + ".appPath") ;
			hosts.add(new Host(address, appPath)) ;
		}
	}

	public Vector<Host> getHosts() {
		return hosts;
	}

}
