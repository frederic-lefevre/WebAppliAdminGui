package com.ibm.lge.fl.webAppliAdmin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.RunningContext;

class ApiEndPointManagerTest {

	@Test
	void test() {
		
		RunningContext 	   runContext = TestUtils.getRunningContext() ;
		AdvancedProperties apiProps   = TestUtils.getApiProperties() ;
		
		ApiEndPointManager endPointMgr = new ApiEndPointManager(apiProps, "webAppli.tester.endPoint.", runContext.getpLog()) ;
		
		Vector<ApiEndPoint> endPoints = endPointMgr.getApiEndPoints() ;
		assertNotNull(endPoints) ;
		assertTrue(endPoints.size() > 0) ;
	}

}
