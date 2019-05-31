package com.ibm.lge.fl.webAppliAdmin;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.lge.fl.util.AdvancedProperties;

public class LogInterface {
	
	// JSON response fields and values
	private final static String OPERATION 	= "operation" ;
	private final static String OK 			= "OK" ;
	private final static String DATA		= "data" ;
		
	private final static String NAME_PROP 		= ".name" ;
	private final static String GET_PROP 		= ".get.url" ;
	private final static String DELETE_PROP 	= ".delete.url" ;
	private final static String PUT_PROP 		= ".put.url" ;
	private final static String GETLEVELS_PROP 	= ".getLevels.url" ;
	private final static String GETOPINFO_PROP 	= ".getOperatingInfo.url" ;
	private final static String GETSMINFO_PROP 	= ".getSmartEnginesInfo.url" ;
	private final static String CHARSET_PROP		= ".charset" ;
	
	private final String getUrl ;
	private final String deleteUrl ;
	private final String putUrl ;
	private final String getLevelsUrl ;
	private final String getOperatingInfoUrl ;
	private final String getSmartEngInfoUrl ;
	private final String name ;
	private final Logger lLog ;
	
	private final HttpRequest getLogApiRequest ;
	private final HttpRequest deleteLogApiRequest ;
	private final HttpRequest deleteResizeLogApiRequest ;
	private final HttpRequest putLogApiRequest ;
	private final HttpRequest getLevelsLogApiRequest ;
	private final HttpRequest getOperatingInfoLogApiRequest ;
	private final HttpRequest getSmartEngInfoLogApiRequest ;
	
	public LogInterface(AdvancedProperties props, String baseProperty, Logger log) {
		
		lLog = log ;
		
		name 	  	 		= props.getProperty(baseProperty + NAME_PROP) ;
		getUrl 	  	 		= props.getProperty(baseProperty + GET_PROP) ;
		deleteUrl 	 		= props.getProperty(baseProperty + DELETE_PROP) ;
		putUrl 	  	 		= props.getProperty(baseProperty + PUT_PROP) ;
		getLevelsUrl 		= props.getProperty(baseProperty + GETLEVELS_PROP) ;
		getOperatingInfoUrl = props.getProperty(baseProperty + GETOPINFO_PROP) ;
		getSmartEngInfoUrl  = props.getProperty(baseProperty + GETSMINFO_PROP) ;
				
		String csString 	= props.getProperty(baseProperty + CHARSET_PROP) ;
		Charset charset ;
		try {
			charset = Charset.forName(csString) ;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception when getting charset for log end points. Chartset default to UTF-8", e);
			charset					= StandardCharsets.UTF_8 ;
		}
		
		HmacGenerator hmacGenerator = new HmacGenerator(props, baseProperty, lLog) ;
		
		getLogApiRequest 			  = new HttpRequest(getUrl, 	 		 HttpRequest.GET_METHOD,	props, hmacGenerator, charset, lLog) ;
		deleteLogApiRequest 		  = new HttpRequest(deleteUrl,			 HttpRequest.DELETE_METHOD, props, hmacGenerator, charset, lLog) ;
		deleteResizeLogApiRequest	  = new HttpRequest(deleteUrl,			 HttpRequest.DELETE_METHOD, props, hmacGenerator, charset, lLog) ;
		putLogApiRequest			  = new HttpRequest(putUrl, 	 		 HttpRequest.PUT_METHOD, 	props, hmacGenerator, charset, lLog) ;
		getLevelsLogApiRequest		  = new HttpRequest(getLevelsUrl,		 HttpRequest.GET_METHOD, 	props, hmacGenerator, charset, lLog) ;
		getOperatingInfoLogApiRequest = new HttpRequest(getOperatingInfoUrl, HttpRequest.GET_METHOD, 	props, hmacGenerator, charset, lLog) ;	
		getSmartEngInfoLogApiRequest  = new HttpRequest(getSmartEngInfoUrl,  HttpRequest.GET_METHOD, 	props, hmacGenerator, charset, lLog) ;	

	}

	public String get(boolean askCompression) {
		
		String logString  ;		
		if (getLogApiRequest.isAvailable()) {
			
			String queryParam ;
			if (askCompression) {
				queryParam = "?compressReturn=true" ;
			} else {
				queryParam = "" ;
			}
			CharBuffer resp = getLogApiRequest.send(queryParam,"") ;
			if (resp != null) {
				logString = resp.toString() ;
			
				try {
					JsonObject jso = new JsonParser().parse(logString).getAsJsonObject() ;
					String rc = jso.get(OPERATION).getAsString() ;
					if (rc.equals(OK)) {
						logString = jso.get(DATA).getAsString() ;
					}
				} catch (Exception e) {
					// must be a simple text response, not json
					if (lLog.isLoggable(Level.FINE)) {
						lLog.log(Level.FINE, "Exception in json parsing get log response. Response will be considered as plain text ", e);
					}
				}
			} else {
				logString = "null response. See application logs." ;
			}
		} else {
			logString = "Connexion to get log API not available" ;
		}
		return logString ;
	}
	
	public String delete() {		
		
		String logString ;
		if (deleteLogApiRequest.isAvailable()) {			
			logString = deleteLogApiRequest.send("","").toString() ; 					
		} else {
			logString = "Connexion to delete log API not available" ;
		}
		return logString ;
	}

	public String deleteResize(String newSize) {		
		
		String logString ;
		if (deleteResizeLogApiRequest.isAvailable()) {
			logString = deleteResizeLogApiRequest.send("/" + newSize, "").toString() ; 					
		} else {
			logString = "Connexion to delete log API not available" ;
		}
		return logString ;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "Server " + name ;
	}
	
	public String changeLevel(JsonObject logParamJson) {
				
		String logString ;
		if (putLogApiRequest.isAvailable()) {
			
			try {
				
				logString = putLogApiRequest.send("", logParamJson.toString()).toString() ;
								
			} catch (Exception e) {
				logString = "Exception in put log send" + e.getMessage() ;
				lLog.log(Level.SEVERE, "Exception in put log send", e);
			}
		
		} else {
			logString = "Connexion to put (change parameters) log API not available" ;
		}
		return logString ;

	}
	
	public String getLevels() {
				
		String logLevelsString  ;		
		if (getLevelsLogApiRequest.isAvailable()) {			
			logLevelsString = getLevelsLogApiRequest.send("", "").toString() ;		
		} else {
			logLevelsString = "Connexion to get levels log API not available" ;
		}
		
		return logLevelsString ;
	}
	
	public String getOperatingInfos() {
		
		String opearatingInfoString  ;		
		if (getOperatingInfoLogApiRequest.isAvailable()) {			
			opearatingInfoString = getOperatingInfoLogApiRequest.send("", "").toString() ;		
		} else {
			opearatingInfoString = "Connexion to get operating infos API not available" ;
		}
		
		return opearatingInfoString ;
	}
	
	public String getSmartEnginesInfos() {
		
		String smartEnginesInfoString  ;		
		if (getOperatingInfoLogApiRequest.isAvailable()) {			
			smartEnginesInfoString = getSmartEngInfoLogApiRequest.send("", "").toString() ;		
		} else {
			smartEnginesInfoString = "Connexion to get smart engines infos API not available" ;
		}
		
		return smartEnginesInfoString ;
	}
}
