/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.webAppliAdmin;

import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fl.util.AdvancedProperties;

public class LogInterface {
	
	private static final Logger lLog = Control.getLogger();
	
	// JSON response fields and values
	private final static String OPERATION = "operation";
	private final static String OK = "OK";
	private final static String DATA = "data";

	private final static String NAME_PROP = ".name";
	private final static String GET_PROP = ".get.url";
	private final static String DELETE_PROP = ".delete.url";
	private final static String PUT_PROP = ".put.url";
	private final static String GETLEVELS_PROP = ".getLevels.url";
	private final static String GETOPINFO_PROP = ".getOperatingInfo.url";
	private final static String GETSMINFO_PROP = ".getSmartEnginesInfo.url";
	private final static String CHARSET_PROP = ".charset";

	private final String getUrl;
	private final String deleteUrl;
	private final String putUrl;
	private final String getLevelsUrl;
	private final String getOperatingInfoUrl;
	private final String getSmartEngInfoUrl;
	private final String name;

	private final HttpExchange getLogApiRequest;
	private final HttpExchange deleteLogApiRequest;
	private final HttpExchange deleteResizeLogApiRequest;
	private final HttpExchange putLogApiRequest;
	private final HttpExchange getLevelsLogApiRequest;
	private final HttpExchange getOperatingInfoLogApiRequest;
	private final HttpExchange getSmartEngInfoLogApiRequest;

	private final static HttpClient httpClient = HttpClient.newHttpClient();
	
	public LogInterface(AdvancedProperties props, String baseProperty) {

		name = props.getProperty(baseProperty + NAME_PROP);
		getUrl = props.getProperty(baseProperty + GET_PROP);
		deleteUrl = props.getProperty(baseProperty + DELETE_PROP);
		putUrl = props.getProperty(baseProperty + PUT_PROP);
		getLevelsUrl = props.getProperty(baseProperty + GETLEVELS_PROP);
		getOperatingInfoUrl = props.getProperty(baseProperty + GETOPINFO_PROP);
		getSmartEngInfoUrl = props.getProperty(baseProperty + GETSMINFO_PROP);

		String csString = props.getProperty(baseProperty + CHARSET_PROP);
		Charset charset;
		try {
			charset = Charset.forName(csString);
		} catch (Exception e) {
			lLog.log(Level.SEVERE, "Exception when getting charset for log end points. Chartset default to UTF-8", e);
			charset = StandardCharsets.UTF_8;
		}

		HmacGenerator hmacGenerator = new HmacGenerator(props, baseProperty);

		getLogApiRequest = new HttpExchange(httpClient, getUrl, HttpExchange.GET_METHOD, hmacGenerator, charset);
		deleteLogApiRequest = new HttpExchange(httpClient, deleteUrl, HttpExchange.DELETE_METHOD, hmacGenerator,charset);
		deleteResizeLogApiRequest = new HttpExchange(httpClient, deleteUrl, HttpExchange.DELETE_METHOD, hmacGenerator,charset);
		putLogApiRequest = new HttpExchange(httpClient, putUrl, HttpExchange.PUT_METHOD, hmacGenerator, charset);
		getLevelsLogApiRequest = new HttpExchange(httpClient, getLevelsUrl, HttpExchange.GET_METHOD, hmacGenerator,charset);
		getOperatingInfoLogApiRequest = new HttpExchange(httpClient, getOperatingInfoUrl, HttpExchange.GET_METHOD,hmacGenerator, charset);
		getSmartEngInfoLogApiRequest = new HttpExchange(httpClient, getSmartEngInfoUrl, HttpExchange.GET_METHOD,hmacGenerator, charset);

	}

	public String get(boolean askCompression) {

		String logString;
		if (getLogApiRequest.isAvailable()) {

			String queryParam;
			if (askCompression) {
				queryParam = "?compressReturn=true";
			} else {
				queryParam = "";
			}
			logString = getLogApiRequest.send(queryParam, "");
			if (logString != null) {

				try {
					JsonObject jso = JsonParser.parseString(logString).getAsJsonObject();
					String rc = jso.get(OPERATION).getAsString();
					if (rc.equals(OK)) {
						logString = jso.get(DATA).getAsString();
					}
				} catch (Exception e) {
					// must be a simple text response, not json
					lLog.log(Level.FINE,
							"Exception in json parsing get log response. Response will be considered as plain text ",
							e);
				}
			} else {
				logString = "null response. See application logs.";
			}
		} else {
			logString = "Connexion to get log API not available";
		}
		return logString;
	}
	
	public String delete() {

		String logString;
		if (deleteLogApiRequest.isAvailable()) {
			logString = deleteLogApiRequest.send("", "");
		} else {
			logString = "Connexion to delete log API not available";
		}
		return logString;
	}

	public String deleteResize(String newSize) {

		String logString;
		if (deleteResizeLogApiRequest.isAvailable()) {
			logString = deleteResizeLogApiRequest.send("/" + newSize, "");
		} else {
			logString = "Connexion to delete log API not available";
		}
		return logString;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "Server " + name;
	}
	
	public String changeLevel(JsonObject logParamJson) {
				
		String logString ;
		if (putLogApiRequest.isAvailable()) {
			
			try {
				
				logString = putLogApiRequest.send("", logParamJson.toString()) ;
								
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
			logLevelsString = getLevelsLogApiRequest.send("", "") ;		
		} else {
			logLevelsString = "Connexion to get levels log API not available" ;
		}
		
		return logLevelsString ;
	}
	
	public String getOperatingInfos(boolean withIpLookUp) {
		
		String queryParam ;
		if (withIpLookUp) {
			queryParam = "?IpLookUp=true" ;
		} else {
			queryParam = "" ;
		}
		String opearatingInfoString  ;		
		if (getOperatingInfoLogApiRequest.isAvailable()) {			
			opearatingInfoString = getOperatingInfoLogApiRequest.send(queryParam, "") ;		
		} else {
			opearatingInfoString = "Connexion to get operating infos API not available" ;
		}
		
		return opearatingInfoString ;
	}
	
	public String getSmartEnginesInfos() {
		
		String smartEnginesInfoString  ;		
		if (getOperatingInfoLogApiRequest.isAvailable()) {			
			smartEnginesInfoString = getSmartEngInfoLogApiRequest.send("", "") ;		
		} else {
			smartEnginesInfoString = "Connexion to get smart engines infos API not available" ;
		}
		
		return smartEnginesInfoString ;
	}
}
