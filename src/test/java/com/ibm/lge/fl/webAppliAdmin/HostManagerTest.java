package com.ibm.lge.fl.webAppliAdmin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.AdvancedProperties;

class HostManagerTest {

	@Test
	void test() {
		
		AdvancedProperties apiProps = TestUtils.getApiProperties() ;
		
		HostManager hostMgr = new HostManager(apiProps, "webAppli.tester.host.") ;
		
		Vector<Host> hosts = hostMgr.getHosts() ;
		assertNotNull(hosts) ;
		assertTrue(hosts.size() > 0) ;
	}

}
