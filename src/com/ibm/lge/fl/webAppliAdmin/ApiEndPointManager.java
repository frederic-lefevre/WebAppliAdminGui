package com.ibm.lge.fl.webAppliAdmin;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import com.ibm.lge.fl.util.AdvancedProperties;

public class ApiEndPointManager {

	private Vector<ApiEndPoint> apiEndPoints ;
	
	public ApiEndPointManager(AdvancedProperties props, String baseProperty, Logger aLog) {

		apiEndPoints = new Vector<ApiEndPoint>() ;
		
		ArrayList<String> logsProperties = props.getKeysElements(baseProperty);
		for (String lp : logsProperties) {
			apiEndPoints.add(new ApiEndPoint(props, baseProperty + lp, aLog)) ;
		}
	}

	public Vector<ApiEndPoint> getApiEndPoints() {
		return apiEndPoints;
	}

}
