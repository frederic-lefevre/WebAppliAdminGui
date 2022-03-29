package org.fl.webAppliAdmin;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.fl.util.AdvancedProperties;

public class ApiEndPointManager {

	private final Vector<ApiEndPoint> apiEndPoints ;
	
	public ApiEndPointManager(AdvancedProperties props, String baseProperty, Logger aLog) {

		apiEndPoints = new Vector<ApiEndPoint>() ;
		
		List<String> logsProperties = props.getKeysElements(baseProperty);
		for (String lp : logsProperties) {
			apiEndPoints.add(new ApiEndPoint(props, baseProperty + lp, aLog)) ;
		}
	}

	public Vector<ApiEndPoint> getApiEndPoints() {
		return apiEndPoints;
	}

}
