package com.ibm.lge.fl.webAppliAdmin;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.RunningContext;

class ApiEndPointTest {

	@Test
	void test() {
		
		RunningContext runContext = TestUtils.getRunningContext() ;
		
		ApiEndPoint apiEndPoint = new ApiEndPoint(runContext.getProps(), "", runContext.getpLog()) ;
		assertEquals(StandardCharsets.UTF_8.displayName(), apiEndPoint.getCharset().displayName()) ;
	}

}
