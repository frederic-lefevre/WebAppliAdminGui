package org.fl.webAppliAdmin;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.RunningContext;

class ApiEndPointTest {

	@Test
	void testCharset() {
		
		RunningContext runContext = TestUtils.getRunningContext() ;
		
		Properties props = new Properties() ;
		props.put("test.charset", StandardCharsets.US_ASCII.displayName()) ;
		AdvancedProperties advProps = new AdvancedProperties(props) ;
		
		ApiEndPoint apiEndPoint = new ApiEndPoint(advProps, "test", runContext.getpLog()) ;
		assertEquals(StandardCharsets.US_ASCII.displayName(), apiEndPoint.getCharset().displayName()) ;
	}

}
