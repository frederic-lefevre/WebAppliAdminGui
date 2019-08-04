package com.ibm.lge.fl.webAppliAdmin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.RunningContext;

class ApiEndPointManagerTest {

	@Test
	void test() {
		
		RunningContext runContext = TestUtils.getRunningContext() ;
		
		ApiEndPointManager endPointMgr = new ApiEndPointManager(runContext.getProps(), "webAppli", runContext.getpLog()) ;
		
		Vector<ApiEndPoint> endPoints = endPointMgr.getApiEndPoints() ;
		assertNotNull(endPoints) ;

	}

}
