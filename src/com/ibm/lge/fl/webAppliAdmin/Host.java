package com.ibm.lge.fl.webAppliAdmin;

public class Host {

	private final String address;
	private final String appPath ;
	
	public Host(String ad, String ap) {
		address = ad ;
		appPath = ap ;
	}

	public String toString() {
		return address + appPath ;
	}
	
	public String getHostPart() {
		return address + appPath ;
	}
}
